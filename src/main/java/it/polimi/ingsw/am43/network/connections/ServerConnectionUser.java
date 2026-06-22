package it.polimi.ingsw.am43.network.connections;

/**
 * Callback contract implemented on the client side (by the
 * {@link it.polimi.ingsw.am43.controller.ClientController}). The client-side
 * connection invokes it when the link to the server is lost, so the consumer can
 * trigger a reconnection attempt.
 */
public interface ServerConnectionUser {

    /** Notifies that the connection to the server has been lost. */
    public void notifyDisconnection();
}
