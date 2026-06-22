package it.polimi.ingsw.am43.model.exceptions;

public class GameEndedException extends RuntimeException {
    public GameEndedException(String message) {
        super(message);
    }
}
