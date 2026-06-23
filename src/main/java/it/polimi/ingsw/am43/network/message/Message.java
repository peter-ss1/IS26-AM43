package it.polimi.ingsw.am43.network.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.utils.Task;

/**
 * A server-to-client payload that carries information to act upon, as opposed to a
 * mere {@link Pong}. It splits into {@link Update} (state changes to apply) and
 * {@link Error} (problems to report). Being a {@link Task} over the
 * {@link ClientController}, each message knows how to apply itself on arrival.
 */
@JsonSubTypes({
        @JsonSubTypes.Type(value = Update.class),
        @JsonSubTypes.Type(value = Error.class),
})

public sealed abstract class Message extends DataServerToClient implements Task<ClientController> permits Error, Update {
    /**
     * Applies this message on the client.
     *
     * @param controller the client controller to act upon
     */
    public abstract void execute(ClientController controller);
}
