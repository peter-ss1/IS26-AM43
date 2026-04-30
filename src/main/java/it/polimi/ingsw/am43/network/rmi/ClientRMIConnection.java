package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.BidirectionalConnection;
import it.polimi.ingsw.am43.network.ServerBidirectionalConnection;
import it.polimi.ingsw.am43.network.ServerConnection;
import it.polimi.ingsw.am43.network.SinglePersistentClientConnection;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;

import java.rmi.RemoteException;


public class ClientRMIConnection implements ServerBidirectionalConnection, VirtualClientRmi {
    private final VirtualClientRmi remote;
    private final ServerController serverController;
    private volatile long lastPing;

    public ClientRMIConnection(VirtualClientRmi clientRmi, ServerController serverController){
        this.remote=clientRmi;
        this.serverController=serverController;
        this.lastPing=System.currentTimeMillis();
    }

    public void sendCommand(GameCommand command){
        this.serverController.addToQueue();
    };
    public void sendCommand(ServerCommand command){
        this.serverController.addToQueue();
    };
    public void connect(){

    };
    public void closeServerConnection(){

    };

    public void sendMessage(Message message){
        try {
            this.remote.sendMessage(message);
        }catch (RemoteException e){
            this.notifyDisconnection();
        }

    };

    public long getLastPing() {
        return lastPing;
    }
    public void updateLastPing(){
        this.lastPing=System.currentTimeMillis();
    }
    public void notifyDisconnection(){
    }
}
