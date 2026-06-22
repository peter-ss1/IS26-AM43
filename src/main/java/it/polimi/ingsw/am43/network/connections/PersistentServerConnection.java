package it.polimi.ingsw.am43.network.connections;

/**
 * Client-side view of the connection to the server that is long-lived and
 * liveness-aware. On top of sending commands it can ping the server and track the
 * last received pong (used by the {@link it.polimi.ingsw.am43.client.HeartBeat}).
 * Both the socket and RMI client-side connections implement this interface.
 */
public interface PersistentServerConnection extends ServerConnection {
    /** @return the timestamp (millis) of the last pong received from the server */
    long getLastPong();

    /** Refreshes the last-pong timestamp to the current time. */
    void updateLastPong();

    /** Sends a ping to the server to keep the connection alive. */
    void ping();

    /** Tears the connection down and notifies that the server was lost. */
    void disconnect();
}
