package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.model.board.ModelInterface;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameRecovery implements Serializable {

    private final int lobbyId;
    private final ModelInterface game;
    private final ConcurrentHashMap<UUID, String> clients;

    public GameRecovery(int lobbyId, ModelInterface game, ConcurrentHashMap<UUID, String> clients) {
        this.lobbyId = lobbyId;
        this.game = game;
        this.clients = clients;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public ModelInterface getGame() {
        return game;
    }

    public ConcurrentHashMap<UUID, String> getClients() {
        return clients;
    }
}