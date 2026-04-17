package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.model.board.ModelInterface;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.exceptions.IllegalPlayerInitializationException;
import it.polimi.ingsw.am43.model.exceptions.OutOfTurnException;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;

import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class GameController {
    private final ServerController serverController;
    private final ModelInterface model;
    private final ConcurrentMap<String, VirtualClient> clients;
    private final BlockingQueue<Command> commandQueue;
    private final int lobbyId;
    private boolean gameStarted;

    public GameController(ServerController serverController, ModelInterface model, int lobbyId, VirtualClient client, String nickname) {
        this.serverController = serverController;
        this.model = model;
        this.clients = new ConcurrentHashMap<>();
        this.clients.put(nickname, client);
        this.commandQueue = new LinkedBlockingQueue<>();
        this.lobbyId = lobbyId;
        this.gameStarted = false;
        new Thread(this::executor).start();
    }

    public void addToQueue(Command command) {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void executor() {
        while (true) {
            try {
                Command command = commandQueue.take();
                command.execute(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (RuntimeException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean containsNickname(String nickname) {
        return clients.containsKey(nickname);
    }

    public boolean containsColor(Color color) {
        return clients.keySet().stream()
                .map(model::getPlayerByName)
                .anyMatch(player -> player.getColor().equals(color));
    }

    public int getNumPlayers() {
        return this.model.getNumPlayers();
    }

    public int getCurrentPlayers() {
        return this.clients.size();
    }

    /*public void registerExistingPlayer(VirtualClient client, String nickname) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (nickname == null || nickname.isBlank()) {
            client.sendMessage(new Error.GenericServerError("Nickname cannot be null or blank"));
            return;
        }

        UUID playerId = getPlayerIdByNickname(nickname);

        if (!containsNickname(nickname)) {
            playerId = UUID.randomUUID();
            clients.put(nickname, client);
            playerIdsToNicknames.put(playerId, nickname);
            nicknamesToPlayerIds.put(nickname, playerId);
            serverController.registerPlayerController(playerId, this);
        }

        if (playerId != null) {
            client.sendMessage(new Update.PlayerIdentityUpdate(playerId));
        }

        client.sendMessage(new Update.PlayerAddedUpdate(nickname, model.getPlayerByName(nickname).getColor()));
        broadcast(new Update.LobbyJoinedUpdate(getLobbyId(), getNumPlayers(), getCurrentPlayers()));

        if (getCurrentPlayers() == getNumPlayers() && !gameStarted) {
            startGame();
        }
    }*/

    public void addPlayer(VirtualClient client, String nickname, Color color) throws RemoteException {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (nickname == null || nickname.isBlank()) {
            try {
                client.sendMessage(new Error.GenericServerError("Nickname cannot be null or blank"));
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (color == null) {
            try {
                client.sendMessage(new Error.GenericServerError("Color cannot be null"));
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (gameStarted) {
            try {
                client.sendMessage(new Error.GameAlreadyStartedError());
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (getCurrentPlayers() >= getNumPlayers()) {
            try {
                client.sendMessage(new Error.FullLobbyError());
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (containsNickname(nickname)) {
            try {
                client.sendMessage(new Error.NicknameAlreadyUsedInLobbyError(nickname));
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (containsColor(color)) {
            try {
                client.sendMessage(new Error.ColorAlreadyUsedError(color));
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try {
            this.model.addPlayer(nickname, color);
            UUID playerId = UUID.randomUUID();

            this.clients.put(nickname, client);

            client.sendMessage(new Update.PlayerIdentityUpdate(playerId));
            broadcast(new Update.PlayerAddedUpdate(nickname, color));
            broadcast(new Update.LobbyJoinedUpdate(getLobbyId(), getNumPlayers(), getCurrentPlayers()));

            if (getCurrentPlayers() == getNumPlayers() && !gameStarted) {
                startGame();
            }
        } catch (IllegalPlayerInitializationException e) {
            client.sendMessage(new Error.GenericServerError(e.getMessage()));
        } catch (RuntimeException e) {
            client.sendMessage(new Error.GenericServerError(e.getMessage()));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void startGame() {
        this.gameStarted = true;
        broadcast(new Update.GameStartedUpdate(getLobbyId()));
    }

    public void pickCard(int id, String nickname) throws RemoteException {
        try {
            this.model.pickCard(this.model.getCardById(id), this.model.getPlayerByName(nickname));
            broadcast(new Update.CardPickedUpdate(nickname, id));
        } catch (IllegalMoveException e) {
            this.clients.get(nickname).sendMessage(new Error.IllegalMoveError(e.getMessage()));
        } catch (OutOfTurnException e) {
            this.clients.get(nickname).sendMessage(new Error.IllegalMoveError(e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.clients.get(nickname).sendMessage(new Error.GenericServerError(e.getMessage()));
        }
    }

    public int getLobbyId() {
        return this.lobbyId;
    }

    public void broadcast(Update update) {
        for (VirtualClient client : clients.values()) {
            try {
                client.sendMessage(update);
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void placeTotem(int position, String nickname) throws RemoteException {
        try {
            this.model.placeTotemOnTrack(this.model.getPlayerByName(nickname), position);
            broadcast(new Update.TotemPlacedUpdate(nickname, position));
        } catch (OutOfTurnException e) {
            this.clients.get(nickname).sendMessage(new Error.OutOfTurnError(e.getMessage()));
        } catch (IllegalStateException e) {
            this.clients.get(nickname).sendMessage(new Error.WrongPhaseError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            this.clients.get(nickname).sendMessage(new Error.InvalidTotemPositionError(position, e.getMessage()));
        }
    }

    public void endTurn(String nickname) throws RemoteException {
        try {
            this.model.endCurrentTurn(this.model.getPlayerByName(nickname));
            broadcast(new Update.TurnEndedUpdate(nickname));
        } catch (OutOfTurnException e) {
            this.clients.get(nickname).sendMessage(new Error.OutOfTurnError(e.getMessage()));
        } catch (IllegalStateException e) {
            this.clients.get(nickname).sendMessage(new Error.WrongPhaseError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            this.clients.get(nickname).sendMessage(new Error.CannotEndTurnError(e.getMessage()));
        }
    }

    public void addClient(VirtualClient client) {
        this.clients.put("", client);
    }
}