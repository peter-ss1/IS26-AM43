package it.polimi.ingsw.am43.network.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.client.ClientModel;

import java.io.Serializable;


@JsonSubTypes({
        @JsonSubTypes.Type(value = Update.class),
        @JsonSubTypes.Type(value = Error.class),
})
public sealed abstract class Message extends DataServerToClient permits Error, Update {
    public abstract void execute(ClientController controller);
}
