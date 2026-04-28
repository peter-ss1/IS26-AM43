package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.SinglePersistentClientConnection;
import it.polimi.ingsw.am43.network.VirtualClient;


public class ClientRMIConnection implements SinglePersistentClientConnection {
    private final VirtualClientRmi remote;
    private final ServerController serverController;
    private volatile long lastPing;

    public ClientRMIConnection(VirtualClientRmi clientRmi, ServerController serverController){
        this.remote=clientRmi;
        this.serverController=serverController;
        this.lastPing=System.currentTimeMillis();
    }

    public VirtualClient getRemote() {
        return this.remote;
    }
    public void disconnect(){
        //TODO
    }

    public long getLastPing() {
        return lastPing;
    }
    public void updateLastPing(){
        this.lastPing=System.currentTimeMillis();
    }
    public void notifyDisconnection(){

    }
}
