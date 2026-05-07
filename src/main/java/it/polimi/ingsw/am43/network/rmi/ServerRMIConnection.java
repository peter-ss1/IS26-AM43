package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.client.HeartBeat;
import it.polimi.ingsw.am43.network.message.MessageReceiver;
import it.polimi.ingsw.am43.network.connections.ServerConnectionUser;
import it.polimi.ingsw.am43.network.connections.PersistentServerConnection;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.*;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class ServerRMIConnection implements PersistentServerConnection, VirtualClientRmi  {

    public VirtualServerRMI remote;
    private volatile long lastPong;
    private HeartBeat heartBeat;
    private final UUID playerID;
    private final ServerConnectionUser connectionUser;
    private final MessageReceiver messageReceiver;
    private final String serverIp;
    private final int port;
    private final String accessPointName;

    public ServerRMIConnection(String serverIp, int port, String accessPointName, ServerConnectionUser connectionUser, MessageReceiver messageReceiver, UUID id) {
        this.port=port;
        this.serverIp=serverIp;
        this.accessPointName=accessPointName;
        this.connectionUser=connectionUser;
        this.messageReceiver=messageReceiver;
        this.playerID=id;
        this.lastPong=System.currentTimeMillis();
        this.heartBeat= new HeartBeat(this);
    }

    public boolean open(){
        try {
            Registry registry = LocateRegistry.getRegistry(serverIp, this.port);
            VirtualServerAccessRMI accessRMI = (VirtualServerAccessRMI) registry.lookup(this.accessPointName);
            VirtualClientRmi stub = (VirtualClientRmi) UnicastRemoteObject.exportObject(this,0);
            this.remote=accessRMI.connect(this.playerID,stub);
        }catch (Exception e){
            return false;  //TODO refine
        }
        this.heartBeat.start();
        return true;
    }
    public void close(){
        this.heartBeat.stop();
    }
    public void sendCommand(ServerCommand command){
        try {
            this.remote.sendCommand(command);
        }catch (RemoteException e){this.disconnect();}
        catch (NullPointerException e){System.out.println("connection not already established");}
    }
    public void sendCommand(GameCommand command){
        try {
            this.remote.sendCommand(command);
        }catch (RemoteException e){this.disconnect();}
        catch (NullPointerException e){System.out.println("connection not already established");}
    }
    public void ping(){
        try {
            this.remote.ping();
            this.updateLastPong();
        }catch (RemoteException e){
            //this.disconnect();
        }
    }

    public void sendMessage(Message message) throws RemoteException{
        this.messageReceiver.receiveMessage(message);
    }

    public long getLastPong(){
        return this.lastPong;
    }
    public void updateLastPong(){
        this.lastPong=System.currentTimeMillis();
    }
    public void disconnect(){
        this.heartBeat.stop();
        this.connectionUser.notifyDisconnection();
    }


}
