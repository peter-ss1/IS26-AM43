package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.Update;

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
        GameController gameController = new GameController(this, game, lobbyId, nickname,playerID);
        lobbies.put(lobbyId, gameController);
        try {
            clients.get(playerID).getClient().sendMessage(new Update.LobbyCreatedUpdate(lobbyId, numPlayers));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }



    //TODO: make it a game command
    public void joinLobby(UUID playerID, int lobbyId){
        if (!this.lobbies.containsKey(lobbyId)) {
            try {
                clients.get(playerID).getClient().sendMessage(new Error.LobbyNotFoundError(lobbyId));
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        GameController gameController = lobbies.get(lobbyId);
        try{
            gameController.joinLobby(playerID);
        }catch (RemoteException e){throw new RuntimeException(e);}
        clients.get(playerID).setLobbyId(lobbyId);
        clients.get(playerID).setState(ClientState.PLAYING);
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

    //TODO remove
    public void sendString(UUID playerId, String message) throws RemoteException {
        System.out.println(message + "from " + playerId);
        this.clients.get(playerId).getClient().sendMessage(new Update.StringUpdate(message));

    }

    private VirtualClient getClientByID(UUID playerID) throws IllegalArgumentException{
        if (!this.clients.containsKey(playerID)) throw new IllegalArgumentException("player not registered");
        return this.clients.get(playerID).getClient();
    }
    public void sendMessage(UUID playerID, Message message) throws RemoteException{
        this.getClientByID(playerID).sendMessage(message);
    }


}