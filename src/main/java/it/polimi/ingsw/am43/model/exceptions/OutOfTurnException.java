package it.polimi.ingsw.am43.model.exceptions;

/**
 * Thrown when a player attempts to act outside of their turn.
 */
public class OutOfTurnException extends RuntimeException {
    /**
     * @param message the detail message describing the out-of-turn action
     */
    public OutOfTurnException(String message) {
        super(message);
    }
}
