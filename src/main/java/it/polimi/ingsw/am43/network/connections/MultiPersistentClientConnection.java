package it.polimi.ingsw.am43.network.connections;

import java.util.Iterator;

/**
 * A {@link MultiClientConnection} whose connections are persistent and can be
 * iterated over (e.g. by the {@link it.polimi.ingsw.am43.network.Reaper} to check
 * liveness of every client).
 */
public interface MultiPersistentClientConnection extends MultiClientConnection,Iterable<PersistentClientConnection> {
    /**
     * @return an iterator over a snapshot of the currently registered connections
     */
    Iterator<PersistentClientConnection>  iterator();
}
