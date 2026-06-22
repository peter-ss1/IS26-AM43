package it.polimi.ingsw.am43.model.exceptions;

/**
 * Thrown when a player cannot be initialized correctly (e.g. invalid setup data).
 */
public class IllegalPlayerInitializationException extends RuntimeException {
    /**
     * @param message the detail message describing the initialization failure
     */
    public IllegalPlayerInitializationException(String message) {
        super(message);
    }
}
