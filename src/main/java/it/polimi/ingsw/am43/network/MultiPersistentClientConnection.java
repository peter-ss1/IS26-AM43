package it.polimi.ingsw.am43.network;

import java.util.UUID;

public interface MultiPersistentClientConnection extends MultiClientConnection {
    long getLastPing(UUID id);
    void updateLastPing(UUID id);
    void notifyDisconnection();
}
