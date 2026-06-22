package it.polimi.ingsw.am43.client.view.tui;

/**
 * Thrown to indicate that the game has started and activate updated input parsing.
 */
public class GameStartedException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail error message.
     */
    public GameStartedException(String message) {
        super(message);
    }
}
