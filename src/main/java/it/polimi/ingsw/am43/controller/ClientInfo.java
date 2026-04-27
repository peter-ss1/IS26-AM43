package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.network.ClientConnection;
import it.polimi.ingsw.am43.network.VirtualClient;

public class ClientInfo{
    private final VirtualClient client;
    private ClientState state;
    private volatile long lastPing;
    private int lobbyId;

    public ClientInfo(VirtualClient client, ClientState state, int lobbyId) {
        this.client = client;
        this.state = state;
        this.lobbyId = lobbyId;
        this.lastPing=System.currentTimeMillis();
    }

    public VirtualClient getClient() {
        return client;
    }

    public ClientState getState() {
        return state;
    }
    public void setState(ClientState clientState){
        this.state=clientState;
    }

    public long getLastPing(){
        return lastPing;
    }
    public void updateLastPing(){
        this.lastPing=System.currentTimeMillis();
    }


    public int getLobbyId() {
        return lobbyId;
    }
    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

}
