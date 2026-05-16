package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.network.connections.ConnectionHandler;
import it.polimi.ingsw.am43.network.connections.PersistentClientConnection;

import it.polimi.ingsw.am43.network.command.CommandReceiver;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;

import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


public class ClientRMIConnection implements PersistentClientConnection, VirtualServerRMI {
    private final VirtualClientRmi remote;
    private final CommandReceiver commandReceiver;
    private final ConnectionHandler connectionHandler;
    private  final UUID playerId;
    private volatile long lastPing;
    private final AtomicBoolean connected;

    public ClientRMIConnection(UUID id, CommandReceiver commandReceiver, ConnectionHandler connectionHandler, VirtualClientRmi clientRmi){
        this.commandReceiver=commandReceiver;
        this.connectionHandler = connectionHandler;
        this.playerId=id;
        this.remote=clientRmi;
        this.lastPing=System.currentTimeMillis();
        this.connected=new AtomicBoolean(false);
    }

    public void sendCommand(GameCommand command) throws RemoteException{
        this.commandReceiver.receiveCommand(command);
    };
    public void sendCommand(ServerCommand command) throws RemoteException{
        this.commandReceiver.receiveCommand(command);
    };
    public void ping() throws RemoteException{
        this.updateLastPing();
    }

    public void register(){
        if (this.connected.compareAndSet(false,true)){
            this.connectionHandler.connect(this.playerId,this);
        }
    };

    public void sendMessage(Message message){
        try {
            this.remote.sendMessage(message);
        }catch (RemoteException e){
            //this.disconnect();
        }

    };

    public long getLastPing() {
        return lastPing;
    }
    public void updateLastPing(){
        this.lastPing=System.currentTimeMillis();
    }
    public void disconnect(){
        if (this.connected.compareAndSet(true,false)){
            try {
                java.rmi.server.UnicastRemoteObject.unexportObject(this, true);
            } catch (java.rmi.NoSuchObjectException e) {
            }
            this.connectionHandler.disconnect(this.playerId,this);
        }
    }
}
