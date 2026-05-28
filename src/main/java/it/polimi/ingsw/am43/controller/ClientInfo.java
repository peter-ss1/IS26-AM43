package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.network.VirtualClient;

public class ClientInfo{
    private ClientState state;
    private int lobbyId;

    public ClientInfo(ClientState state, int lobbyId) {
        this.state = state;
        this.lobbyId = lobbyId;
    }


    public ClientState getState() {
        return state;
    }
    public void setState(ClientState clientState){
        this.state=clientState;
    }


    public int getLobbyId() {
        return lobbyId;
    }
    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

}
