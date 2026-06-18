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

public class ClientController implements ServerConnectionUser, MessageReceiver {

    private ServerConnection serverConnection;
    private Executor<ClientController> messageExecutor;
    private final UI ui;
    private final ClientModel localModel;
    private AtomicBoolean connected;
    private final UUID playerId;

    public ClientController(UI ui, ClientModel localModel) {
        this.ui = ui;
        this.localModel = localModel;
        this.serverConnection = null;
        this.connected = new AtomicBoolean(false);
        this.playerId = ResiliencyManager.getOrCreateUUID(2);
        this.messageExecutor = new Executor<>(this);
        this.messageExecutor.start();
        this.connected = new AtomicBoolean(false);
    }

    public ClientModel getLocalModel() {
        return this.localModel;
    }

    public void chooseConnectionType(String serverIp, boolean rmi) {
        if (this.connected.compareAndSet(false, true)) {
            PersistentServerConnection connection;
            if (rmi) {
                connection = new ServerRMIConnection(serverIp, 1099, "MesosServer", this, this, this.playerId);
            } else {
                connection = new SocketServerConnection(serverIp, 8081, this, this, this.playerId);
            }
            this.serverConnection = connection;
            connection.open();//TODO wait
        }
    }

    public void disconnect() {
        this.serverConnection.close();
    }

    public void refreshLobbies() {
        this.serverConnection.sendCommand(new ServerCommand.FetchLobbiesCommand(this.playerId));
    }

    public void createLobby(String nickname, Color color, int numPlayers) {
        this.serverConnection.sendCommand(new ServerCommand.CreateLobbyCommand(this.playerId, nickname, color, numPlayers));
    }

    public void joinLobby(int lobbyId) {
        this.serverConnection.sendCommand(new ServerCommand.PickLobbyCommand(this.playerId, lobbyId));
    }

    public void joinGame(String nickname, Color color) {
        this.serverConnection.sendCommand(new GameCommand.PickNameColorCommand(this.playerId, nickname, color));
    }

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

    public void answerRejoin(boolean answer) {
        this.serverConnection.sendCommand(new ServerCommand.RejoinGameCommand(this.playerId, answer));
    }

    public void receiveMessage(Message message) {
        this.messageExecutor.delegate(message);
    }

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

    public UI getView() {
        return this.ui;
    }

    public boolean isDisconnected() {
        return !this.connected.get();
    }
}
