package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.utils.Task;

import java.rmi.RemoteException;
import java.util.UUID;

@JsonSubTypes({
        @JsonSubTypes.Type(value = GameCommand.PickNameColorCommand.class, name = "pickNameColorCommand"),
        @JsonSubTypes.Type(value = GameCommand.PlaceTotemCommand.class, name = "placeTotemCommand"),
        @JsonSubTypes.Type(value = GameCommand.PickCardCommand.class, name = "pickCardCommand"),
        @JsonSubTypes.Type(value = GameCommand.EndTurnCommand.class, name = "endTurnCommand"),
        @JsonSubTypes.Type(value = GameCommand.JoinLobbyCommand.class, name = "joinLobbyCommand"),
        @JsonSubTypes.Type(value = GameCommand.RejoinLobbyCommand.class, name = "rejoinLobbyCommand"),
})
public non-sealed abstract class GameCommand extends Command implements Task<GameController> {

    protected GameCommand(UUID playerId) {
        super(playerId);
    }

    public static class SinglePlayerTimeoutCommand extends GameCommand {
        public SinglePlayerTimeoutCommand() { super(null); }

        @Override
        public void execute(GameController controller) {
            controller.handleSinglePlayerTimeout();
        }
    }

    public static class ConnectionCommand extends GameCommand {
        public ConnectionCommand(UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.handleConnection(this.playerId);
        }
    }

    public static class DisconnectionCommand extends GameCommand {
        public DisconnectionCommand(UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.handleDisconnection(this.playerId);
        }
    }

    public static class RecoveryTimeoutCommand extends GameCommand {
        public RecoveryTimeoutCommand() { super(null); }

        @Override
        public void execute(GameController controller) {
            controller.handleRecoveryTimeout();
        }
    }


    public static class JoinLobbyCommand extends GameCommand {
        public JoinLobbyCommand(@JsonProperty("playerId") UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.handleJoinLobby(this.playerId);
        }
    }

    public static class RejoinLobbyCommand extends GameCommand {
        public RejoinLobbyCommand(@JsonProperty("playerId") UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.handleRejoinLobby(this.playerId);
        }
    }

    public static class PickCardCommand extends GameCommand {
        @JsonProperty("cardId")
        private final int id;

        public PickCardCommand(@JsonProperty("playerId") UUID playerId, @JsonProperty("cardId") int id) {
            super(playerId);
            this.id = id;
        }

        @Override
        public void execute(GameController controller) {
            controller.pickCard(this.id, this.playerId);
        }
    }

    public static class PlaceTotemCommand extends GameCommand {
        @JsonProperty("position")
        private final int position;

        public PlaceTotemCommand(@JsonProperty("playerId") UUID playerId, @JsonProperty("position") int position) {
            super(playerId);
            this.position = position;
        }

        @Override
        public void execute(GameController controller) {
            controller.placeTotem(this.position, this.playerId);
        }
    }

    public static class EndTurnCommand extends GameCommand {
        public EndTurnCommand(@JsonProperty("playerId") UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.endTurn(this.playerId);
        }
    }

    public static class PickNameColorCommand extends GameCommand {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("color")
        private final Color color;

        public PickNameColorCommand(@JsonProperty("playerId") UUID playerID,
                                    @JsonProperty("nickname") String nickname,
                                    @JsonProperty("color") Color color) {
            super(playerID);
            this.nickname = nickname;
            this.color = color;
        }

        @Override
        public void execute(GameController controller) {
            controller.joinGame(this.playerId, this.nickname, this.color);
        }
    }
}