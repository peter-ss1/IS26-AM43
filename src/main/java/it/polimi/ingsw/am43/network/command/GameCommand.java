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
/**
 * Base class for in-game commands, i.e. actions targeting a running game. Each
 * concrete subtype implements {@link Task#execute} by invoking the matching method
 * on the {@link GameController}, following the command pattern (no switch needed in
 * the controller). The Jackson subtypes register the wire-level commands; the
 * remaining nested classes are internal commands produced server-side (connection,
 * disconnection, timeouts) and never travel over the network.
 */
public non-sealed abstract class GameCommand extends Command implements Task<GameController> {

    /**
     * @param playerId the player issuing the command (may be {@code null} for
     *                 internal, system-generated commands)
     */
    protected GameCommand(UUID playerId) {
        super(playerId);
    }

    /** Internal: signals the single-player turn timer expired. */
    public static class SinglePlayerTimeoutCommand extends GameCommand {
        public SinglePlayerTimeoutCommand() { super(null); }

        @Override
        public void execute(GameController controller) {
            controller.handleSinglePlayerTimeout();
        }
    }

    /** Internal: signals that a player has (re)connected to the game. */
    public static class ConnectionCommand extends GameCommand {
        public ConnectionCommand(UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.handleConnection(this.playerId);
        }
    }

    /** Internal: signals that a player has disconnected from the game. */
    public static class DisconnectionCommand extends GameCommand {
        public DisconnectionCommand(UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.handleDisconnection(this.playerId);
        }
    }

    /** Internal: signals that the disconnected-player recovery window expired. */
    public static class RecoveryTimeoutCommand extends GameCommand {
        public RecoveryTimeoutCommand() { super(null); }

        @Override
        public void execute(GameController controller) {
            controller.handleRecoveryTimeout();
        }
    }


    /** Wire command: the player asks to join the lobby. */
    public static class JoinLobbyCommand extends GameCommand {
        public JoinLobbyCommand(@JsonProperty("playerId") UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.handleJoinLobby(this.playerId);
        }
    }

    /** Wire command: the player asks to rejoin the lobby after a disconnection. */
    public static class RejoinLobbyCommand extends GameCommand {
        public RejoinLobbyCommand(@JsonProperty("playerId") UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.handleRejoinLobby(this.playerId);
        }
    }

    /** Wire command: the player picks the card identified by {@code cardId}. */
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

    /** Wire command: the player places a totem at the given board {@code position}. */
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

    /** Wire command: the player ends their turn. */
    public static class EndTurnCommand extends GameCommand {
        public EndTurnCommand(@JsonProperty("playerId") UUID playerId) { super(playerId); }

        @Override
        public void execute(GameController controller) {
            controller.endTurn(this.playerId);
        }
    }

    /** Wire command: the player picks their nickname and color to join the game. */
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