package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.command.ServerCommand.RegisterCommand;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * Base class for commands handled by the ServerController.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dataClientToServerType"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = ServerCommand.FetchLobbiesCommand.class, name = "fetchLobbiesCommand"),
        @JsonSubTypes.Type(value = ServerCommand.CreateLobbyCommand.class, name = "createLobbyCommand"),
        @JsonSubTypes.Type(value = ServerCommand.PickLobbyCommand.class, name = "pickLobbyCommand"),
        @JsonSubTypes.Type(value = RegisterCommand.class, name = "registerCommand"),
})

public non-sealed abstract class ServerCommand extends Command {

    protected ServerCommand(UUID playerId) {
        super(playerId);
    }

    @Override
    public void execute(GameController gameController) throws RemoteException {
        // Server commands are not meant to be executed by GameController.
    }

    public static class FetchLobbiesCommand extends ServerCommand {

        public FetchLobbiesCommand(@JsonProperty("playerID") UUID playerId) {
            super(playerId);
        }

        @Override
        public void execute(ServerController serverController) throws RemoteException {
            serverController.fetchLobbies(this.playerId);
        }
    }

    public static class CreateLobbyCommand extends ServerCommand {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("color")
        private final Color color;
        @JsonProperty("numPlayer")
        private final int numPlayers;

        public CreateLobbyCommand(@JsonProperty("playerId") UUID playerId, @JsonProperty("nickname") String nickname, @JsonProperty("color") Color color, @JsonProperty("numPlayer") int numPlayers) {
            super(playerId);
            this.nickname = nickname;
            this.color = color;
            this.numPlayers = numPlayers;
        }

        @Override
        public void execute(ServerController controller) throws RemoteException {
            controller.createLobby(this.playerId, this.nickname, this.color, this.numPlayers);
        }
    }

    public static class PickLobbyCommand extends ServerCommand {
        @JsonProperty("lobbyId")
        private final int lobbyId;

        public PickLobbyCommand(@JsonProperty("playerId") UUID playerId, @JsonProperty("lobbyId") int lobbyId) {
            super(playerId);
            this.lobbyId = lobbyId;
        }

        @Override
        public void execute(ServerController serverController) throws RemoteException {
            serverController.joinLobby(this.playerId, this.lobbyId);
        }
    }

    public static class RegisterCommand extends ServerCommand {
        public RegisterCommand(@JsonProperty("playerId") UUID playerId) {
            super(playerId);
        }

        @Override
        public void execute(ServerController serverController) {
        }
    }

}