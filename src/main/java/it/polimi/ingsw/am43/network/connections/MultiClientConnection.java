package it.polimi.ingsw.am43.network.connections;

import java.util.UUID;

/**
 * A registry of client connections that can be looked up by player id. It lets the
 * server retrieve the connection associated with a specific player in order to send
 * it a message.
 */
public interface MultiClientConnection{
    /**
     * Returns the connection registered for the given player.
     *
     * @param id the player's unique identifier
     * @return the matching client connection
     */
    ClientConnection getConnection(UUID id);
}
