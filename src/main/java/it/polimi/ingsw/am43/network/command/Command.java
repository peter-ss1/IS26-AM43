package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.UUID;

/**
 * A client-to-server message that carries an action to perform, as opposed to a
 * mere {@link Ping}. Every command knows which player issued it. It splits into
 * {@link GameCommand} (in-game actions) and {@link ServerCommand} (pre-game/lobby
 * actions).
 */
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameCommand.class),
        @JsonSubTypes.Type(value = ServerCommand.class)
})
public abstract sealed class Command extends DataClientToServer permits GameCommand, ServerCommand {
    @JsonProperty("playerId")
    protected final UUID playerId;

    /**
     * @param playerId the unique identifier of the player issuing the command
     */
    protected Command(@JsonProperty("playerId") UUID playerId) {
        this.playerId = playerId;
    }


    /**
     * @return the unique identifier of the player that issued this command
     */
    public UUID getPlayerId() {
        return playerId;
    }
}
