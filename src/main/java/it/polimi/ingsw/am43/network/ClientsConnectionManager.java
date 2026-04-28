package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.controller.ClientInfo;
import it.polimi.ingsw.am43.controller.ClientState;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientsConnectionManager implements MultiPersistentClientConnection{

    private ConcurrentMap<UUID,SinglePersistentClientConnection> connections;
    private Thread reaperLoop;

    public ClientsConnectionManager(){
        this.connections= new ConcurrentHashMap<>();
        this.reaperLoop=new Thread(this::reaper);
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
    public long getLastPing(UUID id){
        return this.connections.get(id).getLastPing();
    };
    public void updateLastPing(UUID id){
        this.connections.get(id).updateLastPing();
    };
    public void notifyDisconnection(){

    };

    public void reaper(){
        long now;
        SinglePersistentClientConnection clientConnection;
        while(true){
            for(UUID id : this.connections.keySet()){
                now=System.currentTimeMillis();
                clientConnection=this.connections.get(id);
                if (now - clientConnection.getLastPing() > 10000) {
                    this.disconnect(id);//TODO implement removal logic
                }
            }
            try {
                Thread.sleep(3000);
            }catch (InterruptedException e){
                break;
            }
        }
    }

}
//TODO implement all exceptions