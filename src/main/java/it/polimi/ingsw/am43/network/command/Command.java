package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;

@JsonSubTypes({
        @JsonSubTypes.Type(value = GameCommand.class),
        @JsonSubTypes.Type(value = ServerCommand.class)
})

public abstract sealed class Command extends DataClientToServer permits GameCommand, ServerCommand {

    protected Command(UUID playerId) {
        super(playerId);
    }

    public abstract void execute(ServerController serverController);
    public abstract void execute(GameController gameController);
}
