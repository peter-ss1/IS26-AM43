package it.polimi.ingsw.am43.controller;

/**
 * Data class that tracks the current operational state and lobby position
 * of a connected client on the server.
 */
public class ClientInfo {
    private ClientState state;
    private int lobbyId;

    /**
     * Constructs a new ClientInfo record with a specified initial state and lobby identifier.
     *
     * @param state   the initial state of the client
     * @param lobbyId the identifier of the lobby the client belongs to, or a default value if none
     */
    public ClientInfo(ClientState state, int lobbyId) {
        this.state = state;
        this.lobbyId = lobbyId;
    }

    /**
     * @return the current state of the client.
     */
    public ClientState getState() {
        return state;
    }

    /**
     * Updates the current state of the client.
     *
     * @param clientState the new state to apply
     */
    public void setState(ClientState clientState) {
        this.state = clientState;
    }

    /**
     * @return the identifier of the lobby the client is currently in.
     */
    public int getLobbyId() {
        return lobbyId;
    }

    /**
     * Sets or updates the lobby identifier for the client.
     *
     * @param lobbyId the unique identifier of the updated lobby
     */
    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }
}
