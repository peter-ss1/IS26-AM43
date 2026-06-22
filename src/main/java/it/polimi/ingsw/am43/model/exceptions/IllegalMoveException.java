package it.polimi.ingsw.am43.model.exceptions;

/**
 * Thrown when a player attempts a move that is not allowed by the game rules.
 */
public class IllegalMoveException extends RuntimeException {
    /**
     * @param message the detail message describing the illegal move
     */
    public IllegalMoveException(String message) {
        super(message);
    }
}
