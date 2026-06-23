package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.model.board.ModelInterface;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A serializable data transfer object (DTO) that captures a snapshot of a game's state.
 * Used by the persistence manager to save and restore active match sessions,
 * client and lobby data in the event of a server crash.
 */
public class GameRecovery implements Serializable {

    private final int lobbyId;
    private final ModelInterface game;
    private final ConcurrentHashMap<UUID, String> clients;

    /**
     * Constructs an immutable snapshot wrapper of the current match state.
     *
     * @param lobbyId the identifier of the lobby being saved
     * @param game    the complete core model state implementing {@link ModelInterface}
     * @param clients the mapping of registered player network IDs to nicknames
     */
    public GameRecovery(int lobbyId, ModelInterface game, ConcurrentHashMap<UUID, String> clients) {
        this.lobbyId = lobbyId;
        this.game = game;
        this.clients = clients;
    }

    /**
     * @return the saved lobby numerical identifier
     */
    public int getLobbyId() {
        return lobbyId;
    }

    /**
     * @return the saved core model state
     */
    public ModelInterface getGame() {
        return game;
    }

    /**
     * @return the saved map linking player network UUIDs to their unique nicknames
     */
    public ConcurrentHashMap<UUID, String> getClients() {
        return clients;
    }
}