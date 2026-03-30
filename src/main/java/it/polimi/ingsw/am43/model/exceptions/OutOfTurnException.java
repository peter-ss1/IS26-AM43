package it.polimi.ingsw.am43.model.exceptions;

public class OutOfTurnException extends RuntimeException {
    public OutOfTurnException(String message) {
        super(message);
    }
}
