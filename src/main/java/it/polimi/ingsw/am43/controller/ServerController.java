package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.exceptions.GameEndedException;
import it.polimi.ingsw.am43.network.command.CommandReceiver;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.connections.ClientConnectionUser;
import it.polimi.ingsw.am43.network.connections.MultiClientConnection;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.Update;
import it.polimi.ingsw.am43.utils.Executor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global server-side controller.
 * Serves as the central coordinator for network access. Manages client states,
 * initializes new match instances, and routes gameplay updates securely
 * to their respective localized {@link GameController} instances.
 */
public class ServerController implements ClientConnectionUser, CommandReceiver {
    private final MultiClientConnection connectionManager;
    private final ConcurrentHashMap<UUID, ClientInfo> clients;
    private final ConcurrentHashMap<Integer, GameController> lobbies;
    private final Executor<ServerController> executor;

    /**
     * Instantiates the central server manager and starts its asynchronous command executor thread.
     *
     * @param connectionManager network adapter managing multiple client endpoints
     */
    public ServerController(MultiClientConnection connectionManager) {
        this.connectionManager = connectionManager;
        this.clients = new ConcurrentHashMap<>();
        this.lobbies = new ConcurrentHashMap<>();
        this.executor = new Executor<>(this);
        this.executor.start();
    }

    /**
     * Flags a client network connection attempt to the command executor queue.
     *
     * @param playerId network UUID of the connecting client
     */
    public void notifyConnection(UUID playerId) {
        this.executor.delegate(new ServerCommand.ConnectionCommand(playerId));
    }

    /**
     * Flags a client network disconnection event to the command executor queue.
     *
     * @param id network UUID of the disconnected client
     */
    public void notifyDisconnection(UUID id) {
        this.executor.delegate(new ServerCommand.DisconnectionCommand(id));
    }

    /**
     * Flags a client state change to the command executor queue.
     *
     * @param id network UUID of the transitioning client
     */
    public void putPlayerChoosing(UUID id) {
        this.executor.delegate(new ServerCommand.PutPlayerChoosingCommand(id));
    }

    /**
     * Flags the conclusion of a specified game to the command executor queue.
     *
     * @param lobbyId numerical identifier of lobby to destroy
     */
    public void notifyEndGame(int lobbyId) {
        this.executor.delegate(new ServerCommand.NotifyEndGameCommand(lobbyId));
    }

    /**
     * Accepts global client requests and delegates them to the dedicated executor thread.
     *
     * @param command incoming client requested task
     */
    public void receiveCommand(ServerCommand command) {
        this.executor.delegate(command);
    }

    /**
     * Intercepts game-specific client requests and dispatches them to the correct game controller.
     * Ignores invalid requests.
     *
     * @param command incoming client requested task
     */
    public void receiveCommand(GameCommand command) {
        ClientInfo info = this.clients.get(command.getPlayerId());
        if (info == null || info.getLobbyId() == 0) return;
        GameController gc = this.lobbies.get(info.getLobbyId());
        if (gc == null) return;
        gc.receiveCommand(command);
    }

    /**
     * Delivers a network message data object to a specified client.
     *
     * @param playerId network UUID of the destination client
     * @param message  data object to deliver
     */
    public void sendMessage(UUID playerId, Message message) {
        try {
            this.connectionManager.getConnection(playerId).sendMessage(message);
        } catch (Exception e) {
        }

    }

    /**
     * Callback method managing client connections. Restores disconnected profiles or initializes
     * new records inside the client map before delivering available lobbies.
     *
     * @param playerId requested network identity
     */
    public void handleConnection(UUID playerId) {
        if (this.clients.containsKey(playerId)) {
            this.clients.get(playerId).setState(ClientState.CHOOSING);
            if (this.clients.get(playerId).getLobbyId() != 0)
                this.lobbies.get(this.clients.get(playerId).getLobbyId()).notifyConnection(playerId);
            else
                this.fetchLobbies(playerId);
            System.out.println("User " + playerId + " reconnected to server");
        } else {
            this.clients.put(playerId, new ClientInfo(ClientState.CHOOSING, 0));
            this.fetchLobbies(playerId);
            System.out.println("User " + playerId + " connected to server");
        }
    }

    /**
     * Callback method managing client disconnections. Updates internal registries
     * and notifies local lobby managers.
     *
     * @param id network UUID of the disconnected client
     */
    public void handleDisconnection(UUID id) {
        ClientInfo client = this.clients.get(id);
        if (client != null) {
            if (client.getState() == ClientState.PLAYING && client.getLobbyId() != 0) {
                this.lobbies.get(client.getLobbyId()).notifyDisconnection(id);
            }
            client.setState(ClientState.DISCONNECTED);
            System.out.println("User " + id + " disconnected from server");
        }
    }

    /**
     * Unbinds an existing client association from its chosen lobby.
     *
     * @param id network UUID of the transitioning client
     */
    public void handlePutPlayerChoosing(UUID id) {
        this.clients.get(id).setLobbyId(0);
    }

    /**
     * Destroys an active lobby from internal registries and releases its
     * active participants back into the main server pool.
     *
     * @param lobbyId unique identifier of the target lobby
     */
    public void handleEndGame(int lobbyId) {
        this.lobbies.remove(lobbyId);
        this.clients.values().stream()
                .filter(c -> c.getLobbyId() == lobbyId)
                .forEach(c -> {
                    c.setLobbyId(0);
                    if (c.getState().equals(ClientState.PLAYING))
                        c.setState(ClientState.CHOOSING);
                });
    }

    /**
     * Gathers all open game lobbies and updates the target client with the current list.
     *
     * @param playerId network UUID of the target client
     */
    public void fetchLobbies(UUID playerId) {
        List<LobbyInfo> availableLobbies = lobbies.values().stream()
                .map(game -> new LobbyInfo(game.getLobbyId(), game.getNumPlayers(), game.getCurrentPlayers()))
                .filter(lobbyInfo -> lobbyInfo.getNumPlayers() != lobbyInfo.getCurrentPlayers())
                .toList();
        this.connectionManager.getConnection(playerId).sendMessage(new Update.AvailableLobbiesUpdate(availableLobbies));
    }

    /**
     * Sanitizes data, generates a new match, binds the creator profile as the owner, and alerts all other
     * choosing clients that a new lobby has opened.
     *
     * @param playerId   network UUID of the creator client
     * @param nickname   the client's chosen name
     * @param color      the client's chosen color
     * @param numPlayers the client's chosen lobby size
     */
    public void createLobby(UUID playerId, String nickname, Color color, int numPlayers) {
        if (nickname.isBlank() || !nickname.matches("^[a-zA-Z0-9]{3,12}$")) {
            this.connectionManager.getConnection(playerId).sendMessage(new Error.LobbyCreationError("Invalid name"));
            return;
        }
        if (color == null) {
            this.connectionManager.getConnection(playerId).sendMessage(new Error.LobbyCreationError("Invalid color"));
            return;
        }
        if (numPlayers < 2 || numPlayers > 5) {
            this.connectionManager.getConnection(playerId).sendMessage(new Error.LobbyCreationError("Invalid number of players"));
            return;
        }
        int lobbyId = lobbies.isEmpty() ? 1 : Collections.max(lobbies.keySet()) + 1;
        Game game = new Game(numPlayers, nickname, color);
        GameController gameController = new GameController(this, game, lobbyId, nickname, playerId);
        this.lobbies.put(lobbyId, gameController);
        this.clients.get(playerId).setLobbyId(lobbyId);
        this.clients.get(playerId).setState(ClientState.PLAYING);
        this.connectionManager.getConnection(playerId).sendMessage(
                new Update.LobbyCreatedUpdate(new LobbyInfo(lobbyId, numPlayers, 1), nickname, color));
        for (Map.Entry<UUID, ClientInfo> entry : clients.entrySet()) {
            if (entry.getValue().getState().equals(ClientState.CHOOSING)) {
                this.connectionManager.getConnection(entry.getKey())
                        .sendMessage(new Update.NewLobbyUpdate(new LobbyInfo(lobbyId, numPlayers, 1)));
            }
        }
    }

    /**
     * Registers a client into chosen game lobby instance if slot rules permit.
     *
     * @param playerId network UUID of the joining client
     * @param lobbyId  the identifier of the chosen lobby
     */
    public void joinLobby(UUID playerId, int lobbyId) {
        if (!this.lobbies.containsKey(lobbyId)) {
            this.connectionManager.getConnection(playerId).sendMessage(new Error.LobbyJoinError("Lobby #" + lobbyId + " could not be found."));
            return;
        }
        GameController gameController = lobbies.get(lobbyId);
        if (gameController.getCurrentPlayers() >= gameController.getNumPlayers()) {
            this.connectionManager.getConnection(playerId).sendMessage(new Error.LobbyJoinError("Lobby #" + lobbyId + " is already full."));
            return;
        }
        try {
            gameController.joinLobby(playerId);
        } catch (GameEndedException e) {
            this.fetchLobbies(playerId);
            return;
        }
        this.clients.get(playerId).setLobbyId(lobbyId);
        this.clients.get(playerId).setState(ClientState.PLAYING);
    }

    /**
     * Handles answers to a rejoining prompt following disconnection.
     *
     * @param playerId network UUID of the responding client
     * @param answer   {@code true} to request room re-entry, {@code false} to reset state back to selection
     */
    public void rejoinLobby(UUID playerId, boolean answer) {
        if (!this.clients.containsKey(playerId)) {
            this.connectionManager.getConnection(playerId).sendMessage(new Error.GenericServerError("player not registered"));
            this.fetchLobbies(playerId);
            return;
        }
        if (this.clients.get(playerId).getLobbyId() == 0) {
            this.fetchLobbies(playerId);
            return;
        }
        if (answer) {
            this.clients.get(playerId).setState(ClientState.PLAYING);
            try {
                this.lobbies.get(this.clients.get(playerId).getLobbyId()).rejoinLobby(playerId);
            } catch (GameEndedException e) {
                this.fetchLobbies(playerId);
            }
        } else {
            this.clients.get(playerId).setLobbyId(0);
            this.fetchLobbies(playerId);
        }

    }

    /**
     * Initializes recovery routines during system boot stage. Re-allocates registries
     * and builds matching {@link GameController} using saved data configurations.
     *
     * @param gameRecovery deserialized recovered match state
     */
    public void recoverLobby(GameRecovery gameRecovery) {
        int lobbyId = gameRecovery.getLobbyId();
        for (UUID id : gameRecovery.getClients().keySet()) {
            this.clients.put(id, new ClientInfo(ClientState.DISCONNECTED, lobbyId));
        }
        GameController gameController = new GameController(this, gameRecovery.getGame(), lobbyId, gameRecovery.getClients());
        this.lobbies.put(lobbyId, gameController);
    }

    /**
     * Dispatches via network a broadcast lobby-related update to all clients in the choosing state.
     *
     * @param update the data object to dispatch
     */
    public void choosingBroadcast(Update update){
        for (Map.Entry<UUID, ClientInfo> entry : clients.entrySet()) {
            if (entry.getValue().getState().equals(ClientState.CHOOSING)) {
                this.connectionManager.getConnection(entry.getKey())
                        .sendMessage(update);
            }
        }
    }
}