package it.polimi.ingsw.am43.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * Remote access point published in the RMI registry: the RMI counterpart of the
 * socket doorman. A client looks this up by name and calls {@link #connect} to
 * perform the handshake, handing over its own stub and receiving back the stub of
 * its server-side connection.
 */
public interface VirtualServerAccessRMI extends Remote {
    /**
     * Handshake entry point: registers the client and returns the stub it should
     * use to talk to the server.
     *
     * @param playerId the client's persistent unique identifier
     * @param client   the client's remote stub, used by the server to push messages
     * @return the server-side connection stub the client will call
     * @throws RemoteException if the remote call fails
     */
    VirtualServerRMI connect(UUID playerId, VirtualClientRmi client) throws RemoteException;
}
