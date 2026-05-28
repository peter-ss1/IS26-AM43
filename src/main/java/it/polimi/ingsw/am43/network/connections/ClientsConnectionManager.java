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
        try {
            PersistentClientConnection conn = this.connections.get(id);
            if (conn != null) return conn;
            throw new IllegalArgumentException("player not registered");
        } finally {
            this.lock.readLock().unlock();
        }
    }//to put protected

    public void connect(UUID id, PersistentClientConnection connection){
        PersistentClientConnection old;
        this.lock.writeLock().lock();
        try {
            old = this.connections.put(id, connection);
        } finally {
            this.lock.writeLock().unlock();
        }

        if (old != null && old != connection) {
            this.connectionUser.notifyDisconnection(id);
        }
        if (old != connection) {
            this.connectionUser.notifyConnection(id);
        }


    };
    public void disconnect(UUID id,PersistentClientConnection connection){
        boolean removed;
        this.lock.writeLock().lock();
        try {
            removed = this.connections.remove(id, connection);
        } finally {
            this.lock.writeLock().unlock();
        }
        if (removed) {
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