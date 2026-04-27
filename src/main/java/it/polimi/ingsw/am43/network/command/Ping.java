package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dataClientToServerType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Ping.class, name = "ping"),
})

public final class Ping extends DataClientToServer {
    public Ping(UUID id) {
        super(id);
    }
}

