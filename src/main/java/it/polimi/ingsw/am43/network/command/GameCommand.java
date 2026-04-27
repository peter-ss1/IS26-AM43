package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.model.enums.Color;

import java.rmi.RemoteException;
import java.util.UUID;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dataClientToServerType"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = GameCommand.PickNameColorCommand.class, name = "pickNameColorCommand"),
})
public non-sealed abstract class GameCommand extends Command {

    protected GameCommand(UUID playerId) {
        super(playerId);
    }

    @Override
    public void execute(ServerController serverController) throws RemoteException {
    }

    public static class PickCardCommand extends GameCommand {
        @JsonProperty("cardID")
        private final int id;

        public PickCardCommand(@JsonProperty("playerID") UUID playerId, @JsonProperty("cardID") int id) throws RemoteException {
            super(playerId);
            this.id = id;
        }

        @Override
        public void execute(GameController controller) throws RemoteException {
            controller.pickCard(this.id, this.playerId);
        }
    }

    public static class PlaceTotemCommand extends GameCommand {
        @JsonProperty("position")
        private final int position;

        public PlaceTotemCommand(@JsonProperty("playerID") UUID playerId, @JsonProperty("position") int position) {
            super(playerId);
            this.position = position;
        }

        @Override
        public void execute(GameController controller) throws RemoteException {
            controller.placeTotem(this.position, this.playerId);
        }
    }

    public static class EndTurnCommand extends GameCommand {

        public EndTurnCommand(@JsonProperty("playerID") UUID playerId) {
            super(playerId);
        }

        @Override
        public void execute(GameController controller) throws RemoteException {
            controller.endTurn(this.playerId);
        }
    }

    public static class PickNameColorCommand extends GameCommand {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("color")
        private final Color color;

        public PickNameColorCommand(@JsonProperty("playerID") UUID playerID, @JsonProperty("nickname") String nickname, @JsonProperty("color") Color color) {
            super(playerID);
            this.nickname = nickname;
            this.color = color;
        }

        @Override
        public void execute(GameController controller) throws RemoteException {
            controller.joinGame(this.playerId, this.nickname, this.color);
        }


    }
}