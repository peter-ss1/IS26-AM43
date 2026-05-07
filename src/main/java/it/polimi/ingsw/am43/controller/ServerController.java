package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.connections.ClientConnectionUser;
import it.polimi.ingsw.am43.network.connections.MultiClientConnection;
import it.polimi.ingsw.am43.network.command.CommandReceiver;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.Update;
import it.polimi.ingsw.am43.utils.Executor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Global server-side controller.
 * It manages open lobbies and dispatches commands either to itself
 * or to a specific GameController.
 */
public class ServerController implements ClientConnectionUser, CommandReceiver {
    private final MultiClientConnection connectionManager;
    private final ConcurrentMap<UUID, ClientInfo> clients;
    private final ConcurrentMap<Integer, GameController> lobbies;
    private final Executor<ServerController> executor;

    public ServerController(MultiClientConnection connectionManager) {
        this.connectionManager= connectionManager;
        this.clients = new ConcurrentHashMap<>();
        this.lobbies = new ConcurrentHashMap<>();
        this.executor= new Executor<>(this);
        this.executor.start();
    }

    public void notifyConnection(UUID playerId) {
        if (this.clients.containsKey(playerId)) {
            this.clients.get(playerId).setState(ClientState.CHOOSING);
            if(this.clients.get(playerId).getLobbyId()!=0)
                this.sendMessage(playerId,new Update.RejoinRequestUpdate());
            System.out.println(playerId.toString() + "reconnected");
        } else {
            this.clients.put(playerId, new ClientInfo(ClientState.CHOOSING, 0));
            System.out.println("client connected");
        }
    }

    public void notifyDisconnection(UUID id){
        ClientInfo client;
        int lobby;
        if ((client=this.clients.get(id))!=null){
            if (client.getState()==ClientState.PLAYING && (lobby=client.getLobbyId())!=0){
                this.lobbies.get(lobby).notifyDisconnection(id);
            }
            client.setState(ClientState.DISCONNECTED);
            System.out.println(id.toString()+"disconnected");
        }

    }

    public void recoverLobby(GameRecovery gameRecovery){
        int lobbyId= gameRecovery.getLobbyId();
        for (UUID id : gameRecovery.getClients().keySet()){
            this.clients.put(id,new ClientInfo(ClientState.DISCONNECTED,lobbyId));
        }
        GameController gameController= new GameController(this, gameRecovery.getGame(), lobbyId,gameRecovery.getClients());
        this.lobbies.put(lobbyId,gameController);
    }

    public void receiveCommand(ServerCommand command){
        this.executor.delegate(command);
    };
    public void receiveCommand(GameCommand command){
        lobbies.get(clients.get(command.getPlayerId()).getLobbyId()).receiveCommand(command);
    };


    public void fetchLobbies(UUID playerId){
        List<LobbyInfo> availableLobbies = lobbies.values().stream()
                .map(game -> new LobbyInfo(game.getLobbyId(), game.getNumPlayers(), game.getCurrentPlayers()))
                .filter(lobbyInfo -> lobbyInfo.getNumPlayers() != lobbyInfo.getCurrentPlayers())
                .toList();
        this.connectionManager.getConnection(playerId).sendMessage(new Update.AvailableLobbiesUpdate(availableLobbies));
    }
    public void createLobby(UUID playerId, String nickname, Color color, int numPlayers){
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
        this.connectionManager.getConnection(playerId).sendMessage(new Update.LobbyCreatedUpdate(new LobbyInfo(lobbyId, numPlayers, 1), nickname, color));
        for (Map.Entry<UUID, ClientInfo> entry : clients.entrySet()) {
            if (entry.getValue().getState().equals(ClientState.CHOOSING)) {
                this.connectionManager.getConnection(entry.getKey()).sendMessage(new Update.NewLobbyUpdate(new LobbyInfo(lobbyId, numPlayers, 1)));
            }
        }
    }
    public void joinLobby(UUID playerId, int lobbyId) {
        if (!this.lobbies.containsKey(lobbyId)) {
            this.connectionManager.getConnection(playerId).sendMessage(new Error.LobbyJoinError("Lobby #" + lobbyId + " could not be found."));
            return;
        }
        GameController gameController = lobbies.get(lobbyId);
        if (gameController.getCurrentPlayers() >= gameController.getNumPlayers()) {
            this.connectionManager.getConnection(playerId).sendMessage(new Error.LobbyJoinError("Lobby #" + lobbyId + " is already full."));
            return;
        }//TODO think about it
        this.clients.get(playerId).setLobbyId(lobbyId);
        this.clients.get(playerId).setState(ClientState.PLAYING);
        gameController.joinLobby(playerId);
        for (Map.Entry<UUID, ClientInfo> entry : clients.entrySet()) {
            if (entry.getValue().getState().equals(ClientState.CHOOSING)) {
                this.connectionManager.getConnection(entry.getKey()).sendMessage(new Update.NewLobbyUpdate(new LobbyInfo(lobbyId, gameController.getNumPlayers(), gameController.getCurrentPlayers())));
            }
        }
    }
    public void rejoinLobby(UUID playerId, boolean answer){
        if (!this.clients.containsKey(playerId)) {
            this.connectionManager.getConnection(playerId).sendMessage(new Error.GenericServerError("player not registered"));
            return;
        }
        if(this.clients.get(playerId).getLobbyId()==0){
            this.connectionManager.getConnection(playerId).sendMessage(new Error.GenericServerError("player not in game"));
            return;
        }
        this.clients.get(playerId).setState(ClientState.PLAYING);
        this.lobbies.get(this.clients.get(playerId).getLobbyId()).rejoinLobby(playerId);
    }

    public void sendMessage(UUID playerId, Message message) {
        this.connectionManager.getConnection(playerId).sendMessage(message);
    }




}