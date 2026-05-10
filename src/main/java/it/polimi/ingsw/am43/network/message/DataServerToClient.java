package it.polimi.ingsw.am43.network.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.Ping;

import java.io.Serializable;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dataServerToClientType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Message.class),
        @JsonSubTypes.Type(value = Pong.class, name = "pong")
})

public abstract sealed class DataServerToClient implements Serializable permits Message, Pong{
}
