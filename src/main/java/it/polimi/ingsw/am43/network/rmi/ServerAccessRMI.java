package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.network.connections.ConnectionFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

/**
 * RMI entry point on the server side (the "doorman"), published in the registry.
 * When a client calls {@link #connect}, it builds the server-side
 * {@link ClientRMIConnection} through the {@link ConnectionFactory}, exports it,
 * activates it and returns its stub to the client.
 */
public class ServerAccessRMI extends UnicastRemoteObject implements VirtualServerAccessRMI {
    private final ConnectionFactory connectionFactory;


    /**
     * @param connectionFactory factory used to build a connection for each client
     * @throws RemoteException if the access point cannot be exported
     */
    public ServerAccessRMI(ConnectionFactory connectionFactory) throws RemoteException {
        this.connectionFactory=connectionFactory;

    }

    /**
     * Handshake: creates the server-side connection bound to the client's stub,
     * exports it, registers it and returns its stub.
     *
     * @param playerID the client's persistent unique identifier
     * @param client   the client's remote stub
     * @return the stub of the newly created server-side connection
     * @throws RemoteException if exporting the connection fails
     */
    @Override
    public VirtualServerRMI connect(UUID playerID, VirtualClientRmi client) throws RemoteException {
        ClientRMIConnection connection = this.connectionFactory.createConnection(playerID, client);
        VirtualServerRMI stub = (VirtualServerRMI) UnicastRemoteObject.exportObject(connection, 0);
        connection.register();
        return stub;
    }
}
