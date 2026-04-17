package it.polimi.ingsw.am43.network.command.server;

import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;

import java.rmi.RemoteException;
import java.util.UUID;

/**
 * Base class for commands handled by the ServerController.
 */
public abstract class ServerCommand extends Command {

    protected ServerCommand(UUID playerId) {
        super(playerId);
    }

    @Override
    public void execute(GameController gameController) {
        // Server commands are not meant to be executed by GameController.
    }

    public static class FetchLobbiesCommand extends ServerCommand {

        public FetchLobbiesCommand(UUID playerId) {
            super(playerId);
        }

        @Override
        public void execute(ServerController serverController) {
            serverController.sendLobbies(this.getPlayerId());
        }
    }

    public static class CreateLobbyCommand extends ServerCommand {
        private final String nickname;
        private final Color color;
        private final int numPlayers;

        public CreateLobbyCommand(UUID playerId, String nickname, Color color, int numPlayers) {
            super(playerId);
            this.nickname = nickname;
            this.color = color;
            this.numPlayers = numPlayers;
        }

        @Override
        public void execute(ServerController controller) {
            controller.createLobby(this.getPlayerId(), nickname, color, numPlayers);
/*
                if (requester != null) {
                    controller.registerLobbyCreator(requester, lobbyId, nickname);
                }
            } catch (RuntimeException e) {
                if (requester != null) {
                    requester.sendMessage(new Error.GenericServerError(e.getMessage()));
                }
            }*/
        }
    }

    public static class PickLobbyCommand extends ServerCommand {
        private final int lobbyId;

        public PickLobbyCommand(UUID playerId, int lobbyId) {
            super(playerId);
            this.lobbyId = lobbyId;
        }

        @Override
        public void execute(ServerController serverController) {
            serverController.joinLobby(this.getPlayerId(), lobbyId);
        }
    }

    public static class RegisterCommand extends ServerCommand {
        public RegisterCommand(UUID playerId) {
            super(playerId);
        }

        @Override
        public void execute(ServerController serverController) {
        }
    }

    public static class StringCommand extends ServerCommand {
        private final String message;
        public StringCommand(UUID playerId, String message) {
            super(playerId);
            this.message = message;
        }
        @Override
        public void execute(ServerController serverController) throws RemoteException {
            serverController.sendString(getPlayerId(), message);
        }
    }

    /*public static class AddPlayerCommand extends ServerCommand {
        private final int lobbyId;
        private final String nickname;
        private final Color color;
        private final VirtualClient requester;

        public AddPlayerCommand(int lobbyId, String nickname, Color color) {
            this(lobbyId, nickname, color, null);
        }

        public AddPlayerCommand(int lobbyId, String nickname, Color color, VirtualClient requester) {
            this.lobbyId = lobbyId;
            this.nickname = nickname;
            this.color = color;
            this.requester = requester;
        }

        @Override
        public void execute(ServerController serverController) {
            if (requester == null) {
                return;
            }
            serverController.addPlayerToLobby(requester, lobbyId, nickname, color);
        }
    }*/
}