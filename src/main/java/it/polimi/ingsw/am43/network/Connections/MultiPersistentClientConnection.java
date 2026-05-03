package it.polimi.ingsw.am43.network.Connections;

import java.util.Iterator;

public interface MultiPersistentClientConnection extends MultiClientConnection,Iterable<PersistentClientConnection> {
    Iterator<PersistentClientConnection>  iterator();
}
