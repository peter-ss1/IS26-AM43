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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameController {
    public final ServerController serverController;
    public final ModelInterface model;
    public final Map<String, VirtualClient> clients;
    public final Map<UUID, String> playerIdsToNicknames;
    public final Map<String, UUID> nicknamesToPlayerIds;
    public final BlockingQueue<Command> commandQueue;
    private boolean gameStarted;

    public GameController(ServerController serverController, ModelInterface model) {
        this.serverController = serverController;
        this.model = model;
        this.clients = new HashMap<>();
        this.playerIdsToNicknames = new HashMap<>();
        this.nicknamesToPlayerIds = new HashMap<>();
        this.commandQueue = new LinkedBlockingQueue<>();
        this.gameStarted = false;
        new Thread(this::executor).start();
    }

    public void addToQueue(Command command) {
        commandQueue.offer(command);
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
            }
        }
    }

    public Map<String, VirtualClient> getClientsByNickname() {
        return clients;
    }

    public boolean containsNickname(String nickname) {
        return clients.containsKey(nickname);
    }

    public boolean containsColor(Color color) {
        return clients.keySet().stream()
                .map(model::getPlayerByName)
                .anyMatch(player -> player.getColor().equals(color));
    }

    public Integer getNumPlayers() {
        return this.model.getNumPlayers();
    }

    public int getCurrentPlayers() {
        return this.clients.size();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public UUID getPlayerIdByNickname(String nickname) {
        return nicknamesToPlayerIds.get(nickname);
    }

    public String getNicknameByPlayerId(UUID playerId) {
        return playerIdsToNicknames.get(playerId);
    }

    public void registerExistingPlayer(VirtualClient client, String nickname) {
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
    }

    public void addPlayer(VirtualClient client, String nickname, Color color) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (nickname == null || nickname.isBlank()) {
            client.sendMessage(new Error.GenericServerError("Nickname cannot be null or blank"));
            return;
        }
        if (color == null) {
            client.sendMessage(new Error.GenericServerError("Color cannot be null"));
            return;
        }
        if (gameStarted) {
            client.sendMessage(new Error.GameAlreadyStartedError());
            return;
        }
        if (getCurrentPlayers() >= getNumPlayers()) {
            client.sendMessage(new Error.FullLobbyError());
            return;
        }
        if (containsNickname(nickname)) {
            client.sendMessage(new Error.NicknameAlreadyUsedInLobbyError(nickname));
            return;
        }
        if (containsColor(color)) {
            client.sendMessage(new Error.ColorAlreadyUsedError(color));
            return;
        }

        try {
            this.model.addPlayer(nickname, color);
            UUID playerId = UUID.randomUUID();

            this.clients.put(nickname, client);
            this.playerIdsToNicknames.put(playerId, nickname);
            this.nicknamesToPlayerIds.put(nickname, playerId);
            this.serverController.registerPlayerController(playerId, this);

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
        }
    }

    private void startGame() {
        this.gameStarted = true;
        broadcast(new Update.GameStartedUpdate(getLobbyId()));
    }

    public void pickCard(int id, String nickname) {
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
        return serverController.getLobbyIdByController(this);
    }

    public void broadcast(Update update) {
        for (VirtualClient client : clients.values()) {
            client.sendMessage(update);
        }
    }

    public void placeTotem(int position, String nickname) {
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

    public void endTurn(String nickname) {
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
}