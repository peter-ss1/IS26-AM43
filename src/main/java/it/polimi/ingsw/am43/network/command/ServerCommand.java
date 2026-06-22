package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.utils.Task;

import java.util.UUID;

/**
 * Base class for pre-game / lobby commands, i.e. actions handled directly by the
 * {@link ServerController} rather than by a specific game. Each concrete subtype
 * implements {@link Task#execute} by invoking the matching controller method
 * (command pattern). The Jackson subtypes register the wire-level commands; the
 * remaining nested classes are internal commands generated server-side and never
 * sent over the network.
 */

@JsonSubTypes({
        @JsonSubTypes.Type(value = ServerCommand.FetchLobbiesCommand.class, name = "fetchLobbiesCommand"),
        @JsonSubTypes.Type(value = ServerCommand.CreateLobbyCommand.class, name = "createLobbyCommand"),
        @JsonSubTypes.Type(value = ServerCommand.PickLobbyCommand.class, name = "pickLobbyCommand"),
        @JsonSubTypes.Type(value = ServerCommand.RejoinGameCommand.class, name = "rejoinGameCommand"),
})
public non-sealed abstract class ServerCommand extends Command implements Task<ServerController> {

    /**
     * @param playerId the player issuing the command (may be {@code null} for
     *                 internal, system-generated commands)
     */
    protected ServerCommand(UUID playerId) {
        super(playerId);
    }

    /** Internal: signals that a player has (re)connected at the server level. */
    public static class ConnectionCommand extends ServerCommand {
        public ConnectionCommand(UUID playerId) { super(playerId); }

        @Override
        public void execute(ServerController controller) {
            controller.handleConnection(this.playerId);
        }
    }

    /** Internal: signals that a player has disconnected at the server level. */
    public static class DisconnectionCommand extends ServerCommand {
        public DisconnectionCommand(UUID playerId) { super(playerId); }

        @Override
        public void execute(ServerController controller) {
            controller.handleDisconnection(this.playerId);
        }
    }

    /** Internal: moves a player into the "choosing" state. */
    public static class PutPlayerChoosingCommand extends ServerCommand {
        public PutPlayerChoosingCommand(UUID playerId) { super(playerId); }

        @Override
        public void execute(ServerController controller) {
            controller.handlePutPlayerChoosing(this.playerId);
        }
    }

    /** Internal: notifies the server that the game in the given lobby has ended. */
    public static class NotifyEndGameCommand extends ServerCommand {
        private final int lobbyId;
        public NotifyEndGameCommand(int lobbyId) {
            super(null);
            this.lobbyId = lobbyId;
        }

        @Override
        public void execute(ServerController controller) {
            controller.handleEndGame(this.lobbyId);
        }
    }

    /** Wire command: the client asks for the list of available lobbies. */
    public static class FetchLobbiesCommand extends ServerCommand {
        @JsonCreator
        public FetchLobbiesCommand(@JsonProperty("playerId") UUID playerId) { super(playerId); }

        @Override
        public void execute(ServerController serverController) {
            serverController.fetchLobbies(this.playerId);
        }
    }

    /** Wire command: the client creates a new lobby with nickname, color and size. */
    public static class CreateLobbyCommand extends ServerCommand {
        @JsonProperty("nickname") private final String nickname;
        @JsonProperty("color") private final Color color;
        @JsonProperty("numPlayer") private final int numPlayers;

        public CreateLobbyCommand(@JsonProperty("playerId") UUID playerId,
                                  @JsonProperty("nickname") String nickname,
                                  @JsonProperty("color") Color color,
                                  @JsonProperty("numPlayer") int numPlayers) {
            super(playerId);
            this.nickname = nickname;
            this.color = color;
            this.numPlayers = numPlayers;
        }

        @Override
        public void execute(ServerController controller) {
            controller.createLobby(this.playerId, this.nickname, this.color, this.numPlayers);
        }
    }

    /** Wire command: the client joins the lobby identified by {@code lobbyId}. */
    public static class PickLobbyCommand extends ServerCommand {
        @JsonProperty("lobbyId") private final int lobbyId;

        public PickLobbyCommand(@JsonProperty("playerId") UUID playerId,
                                @JsonProperty("lobbyId") int lobbyId) {
            super(playerId);
            this.lobbyId = lobbyId;
        }

        @Override
        public void execute(ServerController serverController) {
            serverController.joinLobby(this.playerId, this.lobbyId);
        }
    }

    /** Wire command: the client answers whether to rejoin a previously left game. */
    public static class RejoinGameCommand extends ServerCommand {
        @JsonProperty("answer") private final boolean answer;

        @JsonCreator
        public RejoinGameCommand(@JsonProperty("playerId") UUID playerId,
                                 @JsonProperty("answer") boolean answer) {
            super(playerId);
            this.answer = answer;
        }

        @Override
        public void execute(ServerController serverController) {
            serverController.rejoinLobby(this.playerId, this.answer);
        }
    }
}