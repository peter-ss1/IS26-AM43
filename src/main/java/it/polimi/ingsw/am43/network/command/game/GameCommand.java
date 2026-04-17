package it.polimi.ingsw.am43.network.command.game;

import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.command.Command;

import java.util.UUID;

public abstract class GameCommand extends Command {
    private final UUID playerId;

    protected GameCommand(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public void execute(ServerController serverController) {}

    public UUID getPlayerId() {
        return playerId;
    }

    public static class PickCardCommand extends GameCommand {
        private final int id;
        private final String nickname;

        public PickCardCommand(UUID playerId, int id, String nickname) {
            super(playerId);
            this.id = id;
            this.nickname = nickname;
        }

        @Override
        public void execute(GameController controller) {
            controller.pickCard(this.id, this.nickname);
        }
    }

    public static class PlaceTotemCommand extends GameCommand {
        private final int position;
        private final String nickname;

        public PlaceTotemCommand(UUID playerId, int position, String nickname) {
            super(playerId);
            this.position = position;
            this.nickname = nickname;
        }

        @Override
        public void execute(GameController controller) {
            controller.placeTotem(position, nickname);
        }
    }

    public static class EndTurnCommand extends GameCommand {
        private final String nickname;

        public EndTurnCommand(UUID playerId, String nickname) {
            super(playerId);
            this.nickname = nickname;
        }

        @Override
        public void execute(GameController controller) {
            controller.endTurn(nickname);
        }
    }
}