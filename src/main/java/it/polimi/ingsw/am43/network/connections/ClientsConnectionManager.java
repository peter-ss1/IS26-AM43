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

/**
 * Central registry of all connected clients on the server, keyed by player UUID.
 * It is shared by both transports (socket and RMI): every server-side connection
 * registers and unregisters here. It also owns the {@link Reaper} that watches for
 * dead connections, and notifies a {@link ClientConnectionUser} whenever a client
 * connects or disconnects. Access to the map is guarded by a read/write lock.
 */
public class ClientsConnectionManager implements MultiPersistentClientConnection, ConnectionHandler {

    private final ConcurrentMap<UUID, PersistentClientConnection> connections;
    private ClientConnectionUser connectionUser;
    private final Reaper reaper;
    private final ReadWriteLock lock;

    /** Creates an empty manager and immediately starts its reaper thread. */
    public ClientsConnectionManager(){
        this.connections= new ConcurrentHashMap<>();
        this.reaper=new Reaper(this);
        this.lock=new ReentrantReadWriteLock();
        this.reaper.start();
    }

    /**
     * Sets the consumer notified of connection/disconnection events.
     *
     * @param user the connection user (typically the server controller)
     */
    public void setConnectionUser(ClientConnectionUser user){
        this.connectionUser=user;
    }

    /**
     * Returns the connection currently registered for the given player.
     *
     * @param id the player's unique identifier
     * @return the matching connection
     * @throws IllegalArgumentException if no connection is registered for that id
     */
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

    /**
     * Registers a connection for a player. If a different connection already
     * existed for that id it is treated as a replacement: the old one is reported
     * as disconnected first, then the new one as connected.
     *
     * @param id         the player's unique identifier
     * @param connection the connection to register
     */
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
    /**
     * Unregisters a connection for a player, but only if the registered connection
     * is exactly the given one (so a stale connection cannot evict a newer one).
     * If it was removed, the disconnection is reported to the connection user.
     *
     * @param id         the player's unique identifier
     * @param connection the connection expected to be removed
     */
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

    /**
     * Returns an iterator over a snapshot of the current connections, so callers
     * (e.g. the reaper) can iterate without risking a concurrent modification.
     *
     * @return an iterator over a copy of the registered connections
     */
    public Iterator<PersistentClientConnection> iterator(){
        Iterator<PersistentClientConnection> iterator;
        this.lock.readLock().lock();
        iterator= new HashSet<PersistentClientConnection>(this.connections.values()).iterator();
        this.lock.readLock().unlock();
        return iterator;
    }

}
//TODO implement all exceptions