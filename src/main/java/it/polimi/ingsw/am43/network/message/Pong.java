package it.polimi.ingsw.am43.network.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dataServerToClientType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pong.class, name = "pong"),
})
public final class Pong extends DataServerToClient {
}
