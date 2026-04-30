package it.polimi.ingsw.am43.network.message;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.enums.Color;

import java.util.UUID;

public non-sealed abstract class Error extends Message {
    private final String message;

    protected Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void execute(ClientController controller) {
    }

    public static class IllegalMoveError extends Error {
        public IllegalMoveError(String message) {
            super(message);
        }
    }

    public static class GenericServerError extends Error {
        public GenericServerError(String message) {
            super(message);
        }
    }

    public static class LobbyNotFoundError extends Error {
        private final int lobbyId;

        public LobbyNotFoundError(int lobbyId) {
            super("Lobby not found: " + lobbyId);
            this.lobbyId = lobbyId;
        }

        public int getLobbyId() {
            return lobbyId;
        }
    }

    public static class FullLobbyError extends Error {
        public FullLobbyError() {
            super("Lobby is full");
        }
    }

    public static class NicknameAlreadyUsedInLobbyError extends Error {
        private final String nickname;

        public NicknameAlreadyUsedInLobbyError(String nickname) {
            super("Nickname already used in this lobby: " + nickname);
            this.nickname = nickname;
        }

        public String getNickname() {
            return nickname;
        }
    }

    public static class ColorAlreadyUsedError extends Error {
        private final Color color;

        public ColorAlreadyUsedError(Color color) {
            super("Color already used in this lobby: " + color);
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    public static class GameAlreadyStartedError extends Error {
        public GameAlreadyStartedError() {
            super("Game already started");
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

    public static class InvalidTotemPositionError extends Error {
        private final int position;

        public InvalidTotemPositionError(int position, String message) {
            super(message == null || message.isBlank()
                    ? "Invalid totem position: " + position
                    : message);
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class CannotEndTurnError extends Error {
        public CannotEndTurnError(String message) {
            super(message);
        }
    }

    public static class InvalidNameError extends Error {
        public InvalidNameError(String invalidName) {
            super(invalidName);
        }
    }
}