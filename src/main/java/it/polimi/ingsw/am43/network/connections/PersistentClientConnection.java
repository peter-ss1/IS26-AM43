package it.polimi.ingsw.am43.network.connections;

/**
 * Server-side view of a client connection that is long-lived and liveness-aware.
 * On top of sending messages, it tracks the last received ping (used by the
 * {@link it.polimi.ingsw.am43.network.Reaper}) and can be disconnected. Both the
 * socket and RMI server-side connections implement this interface.
 */
public interface PersistentClientConnection extends ClientConnection {
    /** @return the timestamp (millis) of the last ping received from the client */
    public long getLastPing();

    /** Refreshes the last-ping timestamp to the current time. */
    public void updateLastPing();

    /** Tears the connection down and removes it from the registry. */
    public void disconnect();
}
