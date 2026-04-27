package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.command.Ping;

import java.rmi.RemoteException;

public interface ClientConnection {
    long getLastPing();
    void updateLastPing();
}
