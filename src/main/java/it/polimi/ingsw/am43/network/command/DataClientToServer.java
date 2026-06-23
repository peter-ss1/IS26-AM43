package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
/**
 * Root of everything that travels from a client to the server. It is a sealed,
 * serializable hierarchy whose subtypes are either a {@link Command} or a
 * {@link Ping}. The Jackson annotations enable polymorphic JSON: a
 * {@code dataClientToServerType} discriminator field tells the deserializer which
 * concrete type to rebuild, which is exactly what the socket listener relies on.
 */
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

    /** Default constructor (required for deserialization). */
    public DataClientToServer(){
    }
}
