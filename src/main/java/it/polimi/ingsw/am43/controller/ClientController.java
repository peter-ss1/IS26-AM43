package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.connections.PersistentServerConnection;
import it.polimi.ingsw.am43.network.connections.ServerConnection;
import it.polimi.ingsw.am43.network.connections.ServerConnectionUser;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.MessageReceiver;
import it.polimi.ingsw.am43.network.rmi.ServerRMIConnection;
import it.polimi.ingsw.am43.network.socket.client.SocketServerConnection;
import it.polimi.ingsw.am43.utils.Executor;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main controller class on the client side.
 * Coordinates network communications, delegates incoming messages to an asynchronous executor,
 * and translates UI interactions into commands for the server.
 */
public class ClientController implements ServerConnectionUser, MessageReceiver {

    private ServerConnection serverConnection;
    private Executor<ClientController> messageExecutor;
    private final UI ui;
    private final ClientModel localModel;
    private AtomicBoolean connected;
    private final UUID playerId;

    /**
     * Initializes the client controller with the local model, UI, and a unique player ID.
     * Also begins the background message executor.
     *
     * @param ui          the active user interface instance
     * @param localModel  the client's local representation of the game state
     */
    public ClientController(UI ui, ClientModel localModel) {
        this.ui = ui;
        this.localModel = localModel;
        this.serverConnection = null;
        this.connected = new AtomicBoolean(false);
        this.playerId = ResiliencyManager.getOrCreateUUID(1);
        this.messageExecutor = new Executor<>(this);
        this.messageExecutor.start();
        this.connected = new AtomicBoolean(false);
    }

    /**
     * @return the local client-side game model
     */
    public ClientModel getLocalModel() {
        return this.localModel;
    }

    /**
     * Establishes a persistent connection to the server using either RMI or Socket protocol.
     *
     * @param serverIp the IP address of the target server
     * @param rmi      true to use RMI, false to use Socket
     */
    public void chooseConnectionType(String serverIp, boolean rmi) {
        if (this.connected.compareAndSet(false, true)) {
            PersistentServerConnection connection;
            if (rmi) {
                connection = new ServerRMIConnection(serverIp, 1099, "MesosServer", this, this, this.playerId);
            } else {
                connection = new SocketServerConnection(serverIp, 8081, this, this, this.playerId);
            }
            this.serverConnection = connection;
            connection.open();
        }
    }

    /**
     * Closes the server connection cleanly.
     */
    public void disconnect() {
        this.serverConnection.close();
    }

    /**
     * Dispatches a request to the server to obtain the list of active game lobbies.
     */
    public void refreshLobbies() {
        this.serverConnection.sendCommand(new ServerCommand.FetchLobbiesCommand(this.playerId));
    }

    /**
     * Dispatches a request to the server to create a new game lobby.
     *
     * @param nickname   the client's chosen name
     * @param color      the client's chosen color
     * @param numPlayers the client's chosen lobby size
     */
    public void createLobby(String nickname, Color color, int numPlayers) {
        this.serverConnection.sendCommand(new ServerCommand.CreateLobbyCommand(this.playerId, nickname, color, numPlayers));
    }

    /**
     * Dispatches a request to the server to join an existing game lobby.
     *
     * @param lobbyId the identifier of the chosen lobby
     */
    public void joinLobby(int lobbyId) {
        this.serverConnection.sendCommand(new ServerCommand.PickLobbyCommand(this.playerId, lobbyId));
    }

    /**
     * Dispatches a request to the server to submit the client's player profile choices.
     *
     * @param nickname   the client's chosen name
     * @param color      the client's chosen color
     */
    public void joinGame(String nickname, Color color) {
        this.serverConnection.sendCommand(new GameCommand.PickNameColorCommand(this.playerId, nickname, color));
    }

    /**
     * Dispatches a request to the server to pick a specified card.
     *
     * @param id the numerical identifier of the card to pick
     */
    public void pickCard(int id) {
        if (this.serverConnection == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        if (playerId == null) {
            throw new IllegalStateException("Player id is not set");
        }
        this.serverConnection.sendCommand(new GameCommand.PickCardCommand(this.playerId, id));
    }

    /**
     * Dispatches a request to the server to place the totem on the specified
     * offer track card.
     *
     * @param position the zero-based index of the slot within the offer track
     */
    public void placeTotem(int position) {
        if (this.serverConnection == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        if (playerId == null) {
            throw new IllegalStateException("Player id is not set");
        }
        this.serverConnection.sendCommand(new GameCommand.PlaceTotemCommand(this.playerId, position));
    }

    /**
     * Dispatches a request to the server to conclude the client's turn.
     */
    public void endTurn() {
        if (this.serverConnection == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        if (playerId == null) {
            throw new IllegalStateException("Player id is not set");
        }
        this.serverConnection.sendCommand(new GameCommand.EndTurnCommand(this.playerId));
    }

    /**
     * Dispatches a request to the server to submit the client's answer to a reconnection prompt.
     */
    public void answerRejoin(boolean answer) {
        this.serverConnection.sendCommand(new ServerCommand.RejoinGameCommand(this.playerId, answer));
    }

    /**
     * Receives an incoming network message and delegates its processing to the asynchronous executor thread.
     *
     * @param message the network message object received from the server
     */
    public void receiveMessage(Message message) {
        this.messageExecutor.delegate(message);
    }

    /**
     * Handles connection losses by updating the UI view and attempting a continuous reconnection lifecycle.
     */
    public void notifyDisconnection() {
        if (this.connected.compareAndSet(true, false)) {
            this.ui.showDisconnection();
            this.messageExecutor.stop();
            try {
                this.messageExecutor = new Executor<>(this);
                this.messageExecutor.start();
                this.serverConnection.open();
                this.connected.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the UI view bound to this controller
     */
    public UI getView() {
        return this.ui;
    }

    /**
     * Checks the current connection availability state.
     *
     * @return {@code true} if the connection is down, {@code false} if alive
     */
    public boolean isDisconnected() {
        return !this.connected.get();
    }
}
