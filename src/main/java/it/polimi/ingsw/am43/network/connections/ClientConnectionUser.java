package it.polimi.ingsw.am43.network.connections;

import java.util.UUID;

/**
 * Callback contract implemented by whoever consumes client connections (the
 * {@link it.polimi.ingsw.am43.controller.ServerController}). The connection
 * manager invokes these methods when a client appears or drops, so the consumer
 * can react (e.g. resume a game, mark a player offline).
 */
public interface ClientConnectionUser {

    /**
     * Notifies that the client with the given id has connected.
     *
     * @param id the player's unique identifier
     */
    public void notifyConnection(UUID id);

    /**
     * Notifies that the client with the given id has disconnected.
     *
     * @param id the player's unique identifier
     */
    public void notifyDisconnection(UUID id);

}
