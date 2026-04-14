package it.polimi.ingsw.am43.network.command.game;

import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.command.Command;

public abstract class GameCommand extends Command {
    private final int lobbyId;

    protected GameCommand(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    @Override
    public void execute(ServerController serverController) {}

    public int getLobbyId() {
        return lobbyId;
    }

    public static class PickCardCommand extends GameCommand {
        private final int id;
        private final String nickname;
        public PickCardCommand(int lobbyId, int id, String nickname) {
            super(lobbyId);
            this.id = id;
            this.nickname = nickname;
        }

        @Override
        public void execute(GameController controller) {
            controller.pickCard(this.id, this.nickname);
        }
    }
}
