package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Global server-side controller.
 * It manages open lobbies and dispatches commands either to itself
 * or to a specific GameController.
 */
public class ServerController {
    private final ConcurrentMap<UUID, ClientInfo> clients;
    private final ConcurrentMap<Integer, GameController> lobbies;
    private final BlockingQueue<Command> commandQueue;
    private int nextLobbyId;

    public ServerController() {
        this.clients = new ConcurrentHashMap<>();
        this.lobbies = new ConcurrentHashMap<>();
        this.commandQueue = new LinkedBlockingQueue<>();
        this.nextLobbyId = 1;
        new Thread(this::executor).start();
    }

    public void register(UUID playerId, VirtualClient client) {
        if (this.clients.containsKey(playerId)) {
            //TODO: reconnection logic
        } else {
            this.clients.put(playerId, new ClientInfo(client, ClientState.CHOOSING, 0));
        }
    }

    public void addToQueue(ServerCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToQueue(GameCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        GameController lobbyController = lobbies.get(clients.get(command.getPlayerId()).getLobbyId());
        if (lobbyController == null) {
            throw new IllegalArgumentException("Player " + command.getPlayerId() + " is not registered in any lobby");
        }

        lobbyController.addToQueue(command);
    }

    /*public void registerPlayerController(UUID playerId, GameController controller) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player id cannot be null");
        }
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null");
        }
        playerControllers.put(playerId, controller);
    }

    public GameController getControllerByPlayerId(UUID playerId) {
        return lobbies.get(clients.get(playerId).getLobbyId());
    }*/

    private void executor() {
        while (true) {
            try {
                Command command = commandQueue.take();
                command.execute(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendLobbies(UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }

        List<LobbyInfo> availableLobbies = lobbies.values().stream()
                .map(game -> new LobbyInfo(game.getLobbyId(), game.getNumPlayers(), game.getCurrentPlayers()))
                .toList();

        try {
            clients.get(playerId).getClient().sendMessage(new Update.AvailableLobbiesUpdate(availableLobbies));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void createLobby(UUID playerID, String nickname, Color color, int numPlayers) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be null or blank");
        }
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }

        int lobbyId = nextLobbyId++;
        Game game = new Game(numPlayers, nickname, color);
        GameController gameController = new GameController(this, game, lobbyId, clients.get(playerID).getClient(), nickname);
        lobbies.put(lobbyId, gameController);
        try {
            clients.get(playerID).getClient().sendMessage(new Update.LobbyCreatedUpdate(lobbyId, numPlayers));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsLobby(int lobbyId) {
        return lobbies.containsKey(lobbyId);
    }

    /*public GameController getLobbyController(int lobbyId) {
        return lobbies.get(lobbyId);
    }

    public int getLobbyIdByController(GameController controller) {
        return lobbies.entrySet().stream()
                .filter(entry -> entry.getValue().equals(controller))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Controller is not registered"));
    }*/

    /*public void registerLobbyCreator(VirtualClient client, int lobbyId, String nickname) {
        if (!containsLobby(lobbyId)) {
            client.sendMessage(new Error.LobbyNotFoundError(lobbyId));
            return;
        }

        lobbies.get(lobbyId).registerExistingPlayer(client, nickname);
    }*/
    //TODO: make it a game command
    public void joinLobby(UUID playerID, int lobbyId) {
        if (!containsLobby(lobbyId)) {
            try {
                clients.get(playerID).getClient().sendMessage(new Error.LobbyNotFoundError(lobbyId));
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        GameController gameController = lobbies.get(lobbyId);
        gameController.addClient(clients.get(playerID).getClient());
        try {
            clients.get(playerID).getClient().sendMessage(new Update.LobbyJoinedUpdate(
                    lobbyId,
                    gameController.getNumPlayers(),
                    gameController.getCurrentPlayers()
            ));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendString(UUID playerId, String message) throws RemoteException {
        System.out.println(message + "from " + playerId);
        this.clients.get(playerId).getClient().sendMessage(new Update.StringUpdate(message));

    }

    /*public void addPlayerToLobby(VirtualClient client, int lobbyId, String nickname, Color color) {
        if (!containsLobby(lobbyId)) {
            client.sendMessage(new Error.LobbyNotFoundError(lobbyId));
            return;
        }

        lobbies.get(lobbyId).addPlayer(client, nickname, color);
    }

    public void notifyError(VirtualClient client, String message) {
        if (client != null) {
            client.sendMessage(new Error.GenericServerError(message));
        }
    }*/


}