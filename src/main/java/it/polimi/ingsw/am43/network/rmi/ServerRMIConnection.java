package it.polimi.ingsw.am43.network.rmi;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.client.HeartBeat;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.PersistentServerConnection;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.message.DataServerToClient;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Pong;
import it.polimi.ingsw.am43.network.message.Update;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;
import it.polimi.ingsw.am43.network.socket.client.SocketServerHandler;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.UUID;

public class ServerRMIConnection implements PersistentServerConnection {

    public VirtualServerRMI remote;
    private volatile long lastPong;
    private HeartBeat heartBeat;
    private final UUID playerID;
    private final ClientController clientController;

    public ServerRMIConnection(VirtualServerRMI remote,ClientController clientController,UUID id) {
        this.remote=remote;
        this.clientController=clientController;
        this.playerID=id;
        this.lastPong=System.currentTimeMillis();
        this.heartBeat= new HeartBeat(this,id);
    }

    public void connect() throws RemoteException{
        this.remote.connect(this.playerID, new ClientRMI(this.clientController));
        this.heartBeat.start();
    }
    public void disconnect(){

    }
    public VirtualServer getRemote(){
        return this.remote;
    }
    public void ping()throws RemoteException {
        this.remote.ping(this.playerID);
        this.updateLastPong();
    }
    public long getLastPong(){
        return this.lastPong;
    }
    public void updateLastPong(){
        this.lastPong=System.currentTimeMillis();
    }
    public void notifyDisconnection() {
        this.heartBeat.stop();
        this.clientController.disconnect();
    }

}
