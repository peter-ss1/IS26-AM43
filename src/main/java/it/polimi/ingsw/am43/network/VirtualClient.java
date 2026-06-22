package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Protocol-agnostic contract for everything that can act as a client endpoint,
 * i.e. an object the server is able to push a {@link Message} to. Both the socket
 * and the RMI implementations ultimately implement this interface, which lets the
 * rest of the system talk to a client without knowing the underlying transport.
 * It extends {@link Remote} so the same contract can be exported through RMI.
 */
public interface VirtualClient extends Remote {
    /**
     * Delivers a message to the client.
     *
     * @param message the message to send to the client
     * @throws RemoteException if the call fails while travelling over RMI
     */
    void sendMessage(Message message) throws RemoteException;
}
