package it.polimi.ingsw.am43.network.rmi;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.client.HeartBeat;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.ClientConnection;
import it.polimi.ingsw.am43.network.PersistentServerConnection;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.*;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;
import it.polimi.ingsw.am43.network.socket.client.SocketServerHandler;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.UUID;

public class ServerRMIConnection implements PersistentServerConnection, ClientConnection {

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
        this.heartBeat= new HeartBeat(this);
    }


    @Override
    public void sendCommand(ServerCommand command){
        try {
            this.remote.sendCommand(command);
        }catch (RemoteException e){this.notifyDisconnection();}
    }
    @Override
    public void sendCommand(GameCommand command){
        try {
            this.remote.sendCommand(command);
        }catch (RemoteException e){this.notifyDisconnection();}
    }
    public void connect(){
        try {
            this.remote.connect(this.playerID, new ClientRMI(this));
        }catch (RemoteException e){
            throw new RuntimeException();//TODO customize
        }
        this.heartBeat.start();
    }
    public void closeServerConnection(){

    }

    public void sendMessage(Message message){
        this.clientController.receiveMessage(message);
    }

    public void ping(){
        try {
            this.remote.ping(this.playerID);
            this.updateLastPong();
        }catch (RemoteException e){
            this.notifyDisconnection();
        }
    }
    public long getLastPong(){
        return this.lastPong;
    }
    public void updateLastPong(){
        this.lastPong=System.currentTimeMillis();
    }
    public void notifyDisconnection(){
        this.heartBeat.stop();
        this.clientController.disconnect();
    }


}
