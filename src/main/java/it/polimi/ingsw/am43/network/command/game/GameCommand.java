package it.polimi.ingsw.am43.network.command.game;

import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.command.Command;

import java.rmi.RemoteException;
import java.util.UUID;

public abstract class GameCommand extends Command {

    protected GameCommand(UUID playerId) {
        super(playerId);
    }

    @Override
    public void execute(ServerController serverController) {
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
        public void execute(GameController controller) throws RemoteException {
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
        public void execute(GameController controller) throws RemoteException {
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
        public void execute(GameController controller) throws RemoteException {
            controller.endTurn(nickname);
        }
    }
}