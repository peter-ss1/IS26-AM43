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

/**
 * A server-to-client message reporting that something went wrong. It carries a
 * human-readable description and, by default, shows it as a game error on the
 * client view; concrete subtypes refine how the specific error is presented.
 */
public non-sealed abstract class Error extends Message {
    @JsonProperty("message")
    protected final String message;

    /**
     * @param message the human-readable error description
     */
    protected Error(String message) {
        this.message = message;
    }

    /** @return the human-readable error description */
    public String getMessage() {
        return message;
    }

    /**
     * Shows the error on the client view.
     *
     * @param controller the client controller to act upon
     */
    @Override
    public void execute(ClientController controller) {
        controller.getView().showGameError(this.message);
    }

    /** A requested move was not legal in the current game state. */
    public static class IllegalMoveError extends Error {
        public IllegalMoveError(@JsonProperty("message") String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().showGameError("The command could not be performed because: " + this.message);
        }
    }

    /** A generic, uncategorized server-side failure. */
    public static class GenericServerError extends Error {
        public GenericServerError(@JsonProperty("message") String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().showGameError("Server could not perform command due to: " + this.message);
        }
    }

    /** The player tried to act when it was not their turn. */
    public static class OutOfTurnError extends Error {
        public OutOfTurnError(String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().showGameError("The command could not be performed because: " + this.message);
        }
    }

    /** The action is not allowed in the current game phase. */
    public static class WrongPhaseError extends Error {
        public WrongPhaseError(String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().showGameError("The command could not be performed because: " + this.message);
        }
    }

    /** Creating a lobby failed; shown on the lobby-choice screen. */
    public static class LobbyCreationError extends Error {
        public LobbyCreationError(@JsonProperty("message") String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().handleLobbyChoiceError(this.message, true);
        }
    }

    /** Joining a lobby failed; shown on the lobby-choice screen. */
    public static class LobbyJoinError extends Error {
        public LobbyJoinError(String message) {
            super(message);
        }

        @Override
        public void execute(ClientController controller) {
            controller.getView().handleLobbyChoiceError(this.message, false);
        }
    }

    /** The player identity was not valid when joining a lobby. */
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