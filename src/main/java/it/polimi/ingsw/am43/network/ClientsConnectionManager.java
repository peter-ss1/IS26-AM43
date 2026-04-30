package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.rmi.ClientRMIConnection;
import it.polimi.ingsw.am43.network.rmi.VirtualClientRmi;
import it.polimi.ingsw.am43.network.socket.VirtualClientSocket;
import it.polimi.ingsw.am43.network.socket.server.SocketClientConnection;

import java.net.Socket;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientsConnectionManager implements MultiPersistentClientConnection<ServerBidirectionalConnection>{

    private final ConcurrentMap<UUID,ServerBidirectionalConnection> connections;
    private final Reaper reaper;

    public ClientsConnectionManager(){
        this.connections= new ConcurrentHashMap<>();
        this.reaper=new Reaper(this);
        this.reaper.start();
    }

    public void sendMessage(UUID id,Message message) {
        this.connections.get(id).sendMessage(message);
    }


    public ClientConnection getConnection(UUID id) throws IllegalArgumentException{
        if(this.isRegistered(id)) return this.connections.get(id);
        else throw new IllegalArgumentException("player not registered");
    };
    public ServerBidirectionalConnection getCompleteConnection(UUID id){
        return this.connections.get(id);
    }//to put protected
    public boolean isRegistered(UUID id){
        return this.connections.containsKey(id);
    }
    public void register(UUID id, ServerBidirectionalConnection connection){
        this.connections.put(id,connection);
    };
    public void disconnect(UUID id){

    };
    public Set<UUID> getIds() {
        return this.connections.keySet();
    }

    public long getLastPing(UUID id){
        return this.connections.get(id).getLastPing();
    };
    public void updateLastPing(UUID id){
        this.connections.get(id).updateLastPing();
    };
    public void notifyDisconnection(){

    };

}
//TODO implement all exceptions