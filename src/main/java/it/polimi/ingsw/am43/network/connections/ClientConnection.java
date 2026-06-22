package it.polimi.ingsw.am43.network.connections;

import it.polimi.ingsw.am43.network.message.Message;

/**
 * Minimal server-side view of a connected client: the only thing the server needs
 * to do with a client is push a {@link Message} to it. Richer behaviour (liveness,
 * disconnection) is added by {@link PersistentClientConnection}.
 */
public interface ClientConnection {
    /**
     * Sends a message to the connected client.
     *
     * @param message the message to deliver
     */
    void sendMessage(Message message);
}
