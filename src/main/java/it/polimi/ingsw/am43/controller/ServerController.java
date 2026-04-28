package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.*;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.Update;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private final MultiClientConnection connectionManager;
    private final ConcurrentMap<UUID, ClientInfo> clients;
    private final ConcurrentMap<Integer, GameController> lobbies;
    private final BlockingQueue<Command> commandQueue;

    public ServerController(MultiClientConnection clientConnections) {
        this.connectionManager= clientConnections;
        this.clients = new ConcurrentHashMap<>();
        this.lobbies = new ConcurrentHashMap<>();
        this.commandQueue = new LinkedBlockingQueue<>();
        new Thread(this::executor).start();
    }

    public void register(UUID playerId, SinglePersistentClientConnection connection) {
        if (this.clients.containsKey(playerId)) {
            //TODO: reconnection logic
            System.out.println(playerId.toString());
        } else {
            this.clients.put(playerId, new ClientInfo(ClientState.CHOOSING, 0));
            this.connectionManager.register(playerId, connection);//TODO check for race
            System.out.println("client connected");
        }
    }
    private VirtualClient getClientByID(UUID playerID){
        try {
            return this.connectionManager.getRemote(playerID);
        } catch (IllegalArgumentException e) {
            //sendmessage
            return null;
        }

    }

    public void addToQueue(ServerCommand command) {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void addToQueue(GameCommand command) {
        GameController lobbyController = lobbies.get(clients.get(command.getPlayerId()).getLobbyId());
        if (lobbyController == null) {
            throw new IllegalArgumentException("Player " + command.getPlayerId() + " is not registered in any lobby");
        }
        lobbyController.addToQueue(command);
    }
    private void executor() {
        while (true) {
            try {
                Command command = commandQueue.take();
                command.execute(this);
            } catch (RemoteException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void fetchLobbies(UUID playerId) throws RemoteException {
        List<LobbyInfo> availableLobbies = lobbies.values().stream()
                .map(game -> new LobbyInfo(game.getLobbyId(), game.getNumPlayers(), game.getCurrentPlayers()))
                .filter(lobbyInfo -> lobbyInfo.getNumPlayers() != lobbyInfo.getCurrentPlayers())
                .toList();
        this.getClientByID(playerId).sendMessage(new Update.AvailableLobbiesUpdate(availableLobbies));
    }
    public void createLobby(UUID playerID, String nickname, Color color, int numPlayers) throws RemoteException {
        if (nickname.isBlank()) {
            this.getClientByID(playerID).sendMessage(new Error.InvalidNameError("Invalid Name"));
        }
        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }

        int lobbyId = lobbies.isEmpty() ? 1 : Collections.max(lobbies.keySet()) + 1;
        Game game = new Game(numPlayers, nickname, color);
        GameController gameController = new GameController(this, game, lobbyId, nickname, playerID);
        lobbies.put(lobbyId, gameController);
        this.getClientByID(playerID).sendMessage(new Update.LobbyCreatedUpdate(new LobbyInfo(lobbyId, numPlayers, 1), nickname, color));
        for (Map.Entry<UUID, ClientInfo> entry : clients.entrySet()) {
            if (entry.getValue().getState().equals(ClientState.CHOOSING)) {
                this.getClientByID(entry.getKey()).sendMessage(new Update.NewLobbyUpdate(new LobbyInfo(lobbyId, numPlayers, 1)));
            }
        }
    }
    public void joinLobby(UUID playerID, int lobbyId) throws RemoteException {
        if (!this.lobbies.containsKey(lobbyId)) {
            this.getClientByID(playerID).sendMessage(new Error.LobbyNotFoundError(lobbyId));
            return;
        }
        GameController gameController = lobbies.get(lobbyId);
        if (gameController.joinLobby(playerID)) {
            clients.get(playerID).setLobbyId(lobbyId);
            clients.get(playerID).setState(ClientState.PLAYING);
            this.getClientByID(playerID).sendMessage(new Update.LobbyJoinedUpdate(new LobbyInfo(lobbyId, gameController.getNumPlayers(), gameController.getCurrentPlayers())));
        }
        for (Map.Entry<UUID, ClientInfo> entry : clients.entrySet()) {
            if (entry.getValue().getState().equals(ClientState.CHOOSING)) {
                this.getClientByID(entry.getKey()).sendMessage(new Update.NewLobbyUpdate(new LobbyInfo(lobbyId, gameController.getNumPlayers(), gameController.getCurrentPlayers())));
            }
        }
    }


    public void sendMessage(UUID playerID, Message message) throws RemoteException {
        this.getClientByID(playerID).sendMessage(message);
    }




}