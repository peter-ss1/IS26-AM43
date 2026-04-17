package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.network.VirtualClient;

public class ClientInfo {
    private final VirtualClient client;
    private final ClientState state;
    private int lobbyId;

    public ClientInfo(VirtualClient client, ClientState state, int lobbyId) {
        this.client = client;
        this.state = state;
        this.lobbyId = lobbyId;
    }

    public VirtualClient getClient() {
        return client;
    }

    public ClientState getState() {
        return state;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

}
