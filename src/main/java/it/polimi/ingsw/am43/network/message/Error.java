package it.polimi.ingsw.am43.network.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import it.polimi.ingsw.am43.controller.ClientController;

@JsonSubTypes({
        @JsonSubTypes.Type(value = Error.LobbyCreationError.class, name = "lobbyCreationError"),
        @JsonSubTypes.Type(value = Error.OutOfTurnError.class, name = "outOfTurnError"),
        @JsonSubTypes.Type(value = Error.WrongPhaseError.class, name = "wrongPhaseError"),
        @JsonSubTypes.Type(value = Error.IllegalMoveError.class, name = "illegalMoveError"),
        @JsonSubTypes.Type(value = Error.LobbyJoinError.class, name = "lobbyJoinError"),
        @JsonSubTypes.Type(value = Error.InvalidPlayerError.class, name = "invalidPlayerError"),
        @JsonSubTypes.Type(value = Error.GenericServerError.class, name = "genericServerError"),
})

public non-sealed abstract class Error extends Message {
    @JsonProperty("message")
    protected final String message;

    protected Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void execute(ClientController controller) {
        controller.getView().showGameError(this.message);
    }

    public static class IllegalMoveError extends Error {
        public IllegalMoveError(@JsonProperty("message") String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().showGameError("The command could not be performed because: " + this.message);
        }
    }

    public static class GenericServerError extends Error {
        public GenericServerError(@JsonProperty("message") String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().showGameError("Server could not perform command due to: " + this.message);
        }
    }

    public static class OutOfTurnError extends Error {
        public OutOfTurnError(String message) {
            super(message);
        }
    }

    public static class WrongPhaseError extends Error {
        public WrongPhaseError(String message) {
            super(message);
        }
    }

    public static class LobbyCreationError extends Error {
        public LobbyCreationError(@JsonProperty("message") String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().handleLobbyChoiceError(this.message, true);
        }
    }

    public static class LobbyJoinError extends Error {
        public LobbyJoinError(String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().handleLobbyChoiceError(this.message, false);
        }
    }

    public static class InvalidPlayerError extends Error {
        public InvalidPlayerError(String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().handleLobbyJoinError(this.message);
        }
    }
}