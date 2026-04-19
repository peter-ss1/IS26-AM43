package it.polimi.ingsw.am43.client;

public class LobbyInfo {
    private final int lobbyId;
    private final int numPlayers;
    private final int currentPlayers;

    public LobbyInfo(int lobbyId, int numPlayers, int currentPlayers) {
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
}
