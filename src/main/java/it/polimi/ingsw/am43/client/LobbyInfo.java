package it.polimi.ingsw.am43.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class LobbyInfo implements Serializable {
    private final int lobbyId;
    private final int numPlayers;
    private int currentPlayers;

    public LobbyInfo(@JsonProperty("lobbyId") int lobbyId, @JsonProperty("getNumPlayers") int numPlayers, @JsonProperty("getCurrentPlayers") int currentPlayers) {
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

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

}
