package it.polimi.ingsw.am43.network.command.server;

import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;

/**
 * Base class for commands handled by the ServerController.
 */
public abstract class ServerCommand extends Command {

    @Override
    public void execute(GameController gameController) {
        // Server commands are not meant to be executed by GameController.
    }

    public static class FetchLobbiesCommand extends ServerCommand {
        private final VirtualClient requester;

        public FetchLobbiesCommand() {
            this.requester = null;
        }

        public FetchLobbiesCommand(VirtualClient requester) {
            this.requester = requester;
        }

        @Override
        public void execute(ServerController serverController) {
            if (requester == null) {
                return;
            }
            serverController.sendLobbies(requester);
        }
    }

    public static class CreateLobbyCommand extends ServerCommand {
        private final String nickname;
        private final Color color;
        private final int numPlayers;
        private final VirtualClient requester;

        public CreateLobbyCommand(String nickname, Color color, int numPlayers) {
            this(nickname, color, numPlayers, null);
        }

        public CreateLobbyCommand(String nickname, Color color, int numPlayers, VirtualClient requester) {
            this.nickname = nickname;
            this.color = color;
            this.numPlayers = numPlayers;
            this.requester = requester;
        }

        @Override
        public void execute(ServerController controller) {
            try {
                int lobbyId = controller.createLobby(nickname, color, numPlayers);

                if (requester != null) {
                    requester.sendMessage(new Update.LobbyCreatedUpdate(lobbyId, numPlayers));
                    controller.registerLobbyCreator(requester, lobbyId, nickname);
                }
            } catch (RuntimeException e) {
                if (requester != null) {
                    requester.sendMessage(new Error.GenericServerError(e.getMessage()));
                }
            }
        }
    }

    public static class PickLobbyCommand extends ServerCommand {
        private final int lobbyId;
        private final VirtualClient requester;

        public PickLobbyCommand(int lobbyId) {
            this(lobbyId, null);
        }

        public PickLobbyCommand(int lobbyId, VirtualClient requester) {
            this.lobbyId = lobbyId;
            this.requester = requester;
        }

        @Override
        public void execute(ServerController serverController) {
            if (requester == null) {
                return;
            }
            serverController.joinLobby(requester, lobbyId);
        }
    }

    public static class AddPlayerCommand extends ServerCommand {
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
    }
}