package it.polimi.ingsw.am43.network.connections;

import java.util.Iterator;

public interface MultiPersistentClientConnection extends MultiClientConnection,Iterable<PersistentClientConnection> {
    Iterator<PersistentClientConnection>  iterator();
}
