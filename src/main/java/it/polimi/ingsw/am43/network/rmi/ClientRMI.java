package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.message.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Thin RMI wrapper that would expose a {@link ServerRMIConnection} as a remote
 * client stub by delegating {@link #sendMessage}. It is currently unused: the
 * {@code ServerRMIConnection} exports itself directly, so this class is dead code
 * kept from an earlier design.
 */
public class ClientRMI extends UnicastRemoteObject implements VirtualClientRmi {
    private final ServerRMIConnection connection;

    /**
     * @param connection the client-side connection this stub delegates to
     * @throws RemoteException if the object cannot be exported
     */
    public ClientRMI(ServerRMIConnection connection) throws RemoteException {
        super();
        this.connection= connection;
    }

    /**
     * Delegates an incoming message to the wrapped connection.
     *
     * @param message the message received from the server
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void sendMessage(Message message) throws RemoteException {
        this.connection.sendMessage(message);
    }

}
