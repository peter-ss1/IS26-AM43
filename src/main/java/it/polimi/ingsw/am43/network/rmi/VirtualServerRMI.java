package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * RMI-specific specialization of {@link VirtualServer}, used as the type of the
 * server connection's stub. It adds {@link #ping()}: a synchronous remote call
 * whose successful return doubles as the keep-alive acknowledgement (no separate
 * pong message is needed over RMI).
 */
public interface VirtualServerRMI extends VirtualServer, Remote {
    /**
     * Pings the server to keep the connection alive.
     *
     * @throws RemoteException if the remote call fails
     */
    void ping() throws RemoteException;
}
