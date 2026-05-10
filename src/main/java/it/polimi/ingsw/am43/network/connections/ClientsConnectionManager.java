package it.polimi.ingsw.am43.network.connections;

import it.polimi.ingsw.am43.network.Reaper;

import java.util.HashSet;
import java.util.Iterator;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClientsConnectionManager implements MultiPersistentClientConnection, ConnectionHandler {

    private final ConcurrentMap<UUID, PersistentClientConnection> connections;
    private ClientConnectionUser connectionUser;
    private final Reaper reaper;
    private final ReadWriteLock lock;

    public ClientsConnectionManager(){
        this.connections= new ConcurrentHashMap<>();
        this.reaper=new Reaper(this);
        this.lock=new ReentrantReadWriteLock();
        this.reaper.start();
    }

    public void setConnectionUser(ClientConnectionUser user){
        this.connectionUser=user;
    }
    public ClientConnection getConnection(UUID id) throws IllegalArgumentException{
        this.lock.readLock().lock();
        if(this.connections.containsKey(id)){
            this.lock.readLock().unlock();
            return this.connections.get(id);
        }
        else {
            this.lock.readLock().unlock();
            throw new IllegalArgumentException("player not registered");
        }
    };//to put protected

    public void connect(UUID id, PersistentClientConnection connection){
        this.lock.writeLock().lock();
        if(this.connections.put(id,connection)!=connection){
            this.lock.writeLock().unlock();
            this.connectionUser.notifyConnection(id);
        }

    };
    public void disconnect(UUID id,PersistentClientConnection connection){
        this.lock.writeLock().lock();
        if(this.connections.remove(id,connection)){
            this.lock.writeLock().unlock();
            this.connectionUser.notifyDisconnection(id);
        }
    };

    public Iterator<PersistentClientConnection> iterator(){
        Iterator<PersistentClientConnection> iterator;
        this.lock.readLock().lock();
        iterator= new HashSet<PersistentClientConnection>(this.connections.values()).iterator();
        this.lock.readLock().unlock();
        return iterator;
    }

}
//TODO implement all exceptions