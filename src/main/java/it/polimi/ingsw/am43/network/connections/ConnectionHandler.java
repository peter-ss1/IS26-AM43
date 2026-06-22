package it.polimi.ingsw.am43.network.connections;

import java.util.UUID;

/**
 * Contract used by a server-side connection to register or unregister itself in
 * the connection manager. It decouples a single {@code PersistentClientConnection}
 * from the manager that keeps track of all of them.
 */
public interface ConnectionHandler {

    /**
     * Removes the given connection for the player from the registry.
     *
     * @param id         the player's unique identifier
     * @param connection the connection being torn down
     */
    public void disconnect(UUID id, PersistentClientConnection connection);

    /**
     * Registers the given connection for the player in the registry.
     *
     * @param id         the player's unique identifier
     * @param connection the connection to register
     */
    public void connect(UUID id, PersistentClientConnection connection);
}
