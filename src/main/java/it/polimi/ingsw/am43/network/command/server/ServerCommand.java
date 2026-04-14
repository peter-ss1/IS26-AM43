package it.polimi.ingsw.am43.network.command.server;

import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.command.Command;

import java.util.UUID;

public abstract class ServerCommand extends Command {

    @Override
    public void execute(GameController gameController) {}

    public static class FetchLobbiesCommand extends ServerCommand {
        @Override
        public void execute(ServerController serverController) {
        }
    }

    public static class CreateLobbyCommand extends ServerCommand {
        private final String nickname;
        private final Color color;
        private final int numPlayers;

        public CreateLobbyCommand(String nickname, Color color, int numPlayers) {
            this.nickname = nickname;
            this.color = color;
            this.numPlayers = numPlayers;
        }

        @Override
        public void execute(ServerController controller) {
            controller.createLobby(this.nickname, this.color, this.numPlayers);
        }
    }
}
