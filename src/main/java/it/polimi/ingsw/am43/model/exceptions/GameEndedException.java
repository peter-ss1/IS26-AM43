package it.polimi.ingsw.am43.model.exceptions;

/**
 * Thrown when a player attempts an action on a concluded game
 */
public class GameEndedException extends RuntimeException {
    /**
     * @param message the detail message describing additional details
     */
    public GameEndedException(String message) {
        super(message);
    }
}
