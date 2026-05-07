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
    @JsonProperty("playerId")
    protected final UUID playerId;

    protected Command(@JsonProperty("playerId")UUID playerId) {
        this.playerId=playerId;
    }


    public UUID getPlayerId() {
        return playerId;
    }
}
