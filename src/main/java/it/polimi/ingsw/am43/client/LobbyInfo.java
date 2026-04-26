package it.polimi.ingsw.am43.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record LobbyInfo(int lobbyId, int numPlayers, int currentPlayers) implements Serializable {
    public LobbyInfo(@JsonProperty("lobbyId") int lobbyId, @JsonProperty("numPlayers") int numPlayers, @JsonProperty("currentPlayers") int currentPlayers) {
        this.lobbyId = lobbyId;
        this.numPlayers = numPlayers;
        this.currentPlayers = currentPlayers;
    }
}
