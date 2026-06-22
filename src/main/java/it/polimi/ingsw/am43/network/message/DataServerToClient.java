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

/**
 * Root of everything that travels from the server to a client. It is a sealed,
 * serializable hierarchy whose subtypes are either a {@link Message} or a
 * {@link Pong}. The Jackson annotations enable polymorphic JSON through a
 * {@code dataServerToClientType} discriminator field, which the socket listener
 * uses to rebuild the right concrete type.
 */
public abstract sealed class DataServerToClient implements Serializable permits Message, Pong{
}
