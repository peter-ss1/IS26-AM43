package it.polimi.ingsw.am43.client.view.tui;

/**
 * Thrown to indicate that the client has been disconnected and activate alternative input parsing.
 */
public class DisconnectedException extends Exception {
    /**
     * Constructs a new exception with the specified detail error message.
     */
    public DisconnectedException(String message) {
        super(message);
    }
}
