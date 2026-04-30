package it.polimi.ingsw.am43.network;

import java.util.UUID;

public interface MultiPersistentClientConnection<T extends PersistentClientConnection> extends MultiClientConnection<T> {

    long getLastPing(UUID id);
    void updateLastPing(UUID id);
    void notifyDisconnection();
     T getCompleteConnection(UUID id);
}
