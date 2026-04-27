package it.polimi.ingsw.am43.network;

import java.rmi.RemoteException;
import java.util.UUID;

public interface ServerConnection {
    public VirtualServer getRemote();
    void connect(UUID id) throws RemoteException;
    void disconnect();
}
