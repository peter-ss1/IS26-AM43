package it.polimi.ingsw.am43.network.connections;

import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;

/**
 * Minimal client-side view of the connection to the server: it can send commands
 * and be opened/closed. Liveness behaviour is added by
 * {@link PersistentServerConnection}.
 */
public interface ServerConnection{

    /**
     * Sends an in-game command to the server.
     *
     * @param command the {@link GameCommand} to send
     */
    void sendCommand(GameCommand command);

    /**
     * Sends a pre-game / lobby command to the server.
     *
     * @param command the {@link ServerCommand} to send
     */
    void sendCommand(ServerCommand command);

    /** Establishes the connection to the server (with retries until it succeeds). */
    void open();

    /** Closes the connection, releasing its threads and resources. */
    void close();

}
