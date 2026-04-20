package it.polimi.ingsw.am43.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class LobbyInfo implements Serializable {
    private final int lobbyId;
    private final int numPlayers;
    private final int currentPlayers;

    public LobbyInfo(@JsonProperty("lobbyId") int lobbyId, @JsonProperty("numPlayers") int numPlayers, @JsonProperty("currentPlayers") int currentPlayers) {
        this.lobbyId = lobbyId;
        this.numPlayers = numPlayers;
        this.currentPlayers = currentPlayers;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    @Override
    public String toString() {
        return "ID: " + this.lobbyId + " - NUMERO GIOCATORI: " + this.numPlayers + " - ATTIVI: " + this.currentPlayers;
    }
}
