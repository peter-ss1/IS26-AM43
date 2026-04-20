package it.polimi.ingsw.am43.network.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.client.ClientModel;

import java.io.Serializable;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "messageType"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = Update.class, name = "updateType"),
        @JsonSubTypes.Type(value = Error.class, name = "errorType"),
})
public abstract class Message implements Serializable {
    public abstract void execute(ClientController controller);
}
