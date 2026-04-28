package it.polimi.ingsw.am43.network;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientsConnectionManager implements MultiPersistentClientConnection{

    private final ConcurrentMap<UUID,SinglePersistentClientConnection> connections;
    private final Reaper reaper;

    public ClientsConnectionManager(){
        this.connections= new ConcurrentHashMap<>();
        this.reaper=new Reaper(this);
        this.reaper.start();
    }

    public VirtualClient getRemote(UUID id) throws IllegalArgumentException{
        if(this.isRegistered(id)) return this.connections.get(id).getRemote();
        else throw new IllegalArgumentException("player not registered");
    };
    public boolean isRegistered(UUID id){
        return this.connections.containsKey(id);
    }
    public void register(UUID id, SinglePersistentClientConnection connection){
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