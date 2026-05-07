package it.polimi.ingsw.am43.network.connections;

import it.polimi.ingsw.am43.network.Reaper;

import java.util.Iterator;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientsConnectionManager implements MultiPersistentClientConnection, ConnectionHandler {

    private final ConcurrentMap<UUID, PersistentClientConnection> connections;
    private ClientConnectionUser connectionUser;
    private final Reaper reaper;

    public ClientsConnectionManager(){
        this.connections= new ConcurrentHashMap<>();
        this.reaper=new Reaper(this);
        this.reaper.start();
    }

    public void setConnectionUser(ClientConnectionUser user){
        this.connectionUser=user;
    }
    public ClientConnection getConnection(UUID id) throws IllegalArgumentException{
        if(this.connections.containsKey(id)) return this.connections.get(id);
        else throw new IllegalArgumentException("player not registered");
    };//to put protected

    public void connect(UUID id, PersistentClientConnection connection){
        this.connections.put(id,connection);
        this.connectionUser.notifyConnection(id);
    };
    public void disconnect(UUID id){
        if(this.connections.remove(id)!=null){
            this.connectionUser.notifyDisconnection(id);
        }
    };

    public Iterator<PersistentClientConnection> iterator(){
        return this.connections.values().iterator();
    }

}
//TODO implement all exceptions