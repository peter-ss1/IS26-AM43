package it.polimi.ingsw.am43.network;

import java.rmi.RemoteException;
import java.util.UUID;

public interface PersistentServerConnection extends ServerConnection {
    long getLastPong();
    void updateLastPong();
    void ping();
    void notifyDisconnection();
}
