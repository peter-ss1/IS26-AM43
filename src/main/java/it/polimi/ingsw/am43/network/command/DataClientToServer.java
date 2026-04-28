package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dataClientToServerType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Command.class),
        @JsonSubTypes.Type(value = Ping.class, name = "ping")
})

public abstract sealed class DataClientToServer implements Serializable permits Command, Ping{

    protected final UUID playerId;

    public DataClientToServer(UUID id){
        this.playerId=id;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
