package it.polimi.ingsw.am43.model.exceptions;

public class InvalidNicknameException extends RuntimeException {
    public InvalidNicknameException(String nicknameIsAlreadyInUse) {
    }
}
