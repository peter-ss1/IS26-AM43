package it.polimi.ingsw.am43.network.message;

/**
 * Sink for messages arriving from the server, implemented by the
 * {@link it.polimi.ingsw.am43.controller.ClientController}. Client-side connections
 * forward every incoming message here, regardless of the transport.
 */
public interface MessageReceiver {
    /**
     * Receives a message from the server.
     *
     * @param message the incoming message
     */
    public void receiveMessage(Message message);
}
