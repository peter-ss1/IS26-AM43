package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.rmi.ClientRMI;
import it.polimi.ingsw.am43.network.rmi.VirtualServerRMI;
import it.polimi.ingsw.am43.network.socket.client.ServerSocketHandler;
import it.polimi.ingsw.am43.network.socket.client.SocketClient;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientController {
    private VirtualServer server;
    private final UI ui;
    private final ClientModel localModel;
    private final BlockingQueue<Message> messageQueue;
    private final UUID playerId;

    public ClientController(UI ui, ClientModel localModel) {
        this.ui = ui;
        this.localModel = localModel;
        this.server = null;
        this.playerId = UUID.randomUUID();
        this.messageQueue = new LinkedBlockingQueue<>();
        new Thread(this::executor).start();
    }

    private void executor() {
        while (true) {
            try {
                Message message = messageQueue.take();
                message.execute(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public ClientModel getLocalModel() {
        return this.localModel;
    }

    public void chooseConnectionType(boolean rmi) throws IOException {
        if (rmi) {
            try {
                Registry registry = LocateRegistry.getRegistry(InetAddress.getLocalHost().getHostAddress(), 1099);
                this.server = (VirtualServerRMI) registry.lookup("MesosServer");
                ((VirtualServerRMI) this.server).connect(this.playerId, new ClientRMI(this));
                this.ui.showMessage("Successfully connected to server via RMI.");
            } catch (NotBoundException e) {
                this.ui.showMessage("Error: Could not connect to server via RMI.");
            }
        } else {
            Socket serverSocket;
            try {
                serverSocket = new Socket(InetAddress.getLocalHost().getHostAddress(), 8080);
            } catch (IOException e) {
                this.ui.showMessage("Error: Could not connect to server via Socket.");
                return;
            }
            InputStreamReader socketRx = new InputStreamReader(serverSocket.getInputStream());
            OutputStreamWriter socketTx = new OutputStreamWriter(serverSocket.getOutputStream());
            this.server = new ServerSocketHandler(new BufferedWriter(socketTx));
            new SocketClient(new BufferedReader(socketRx), this).run();
            this.server.sendCommand(new ServerCommand.RegisterCommand(this.playerId));
            this.ui.showMessage("Successfully connected to server via Socket.");
        }
    }

    public void refreshLobbies() throws RemoteException {
        server.sendCommand(new ServerCommand.FetchLobbiesCommand(this.playerId));
    }

    public void createLobby(String nickname, Color color, int numPlayers) throws RemoteException {
        server.sendCommand(new ServerCommand.CreateLobbyCommand(this.playerId, nickname, color, numPlayers));
    }

    public void joinLobby(int lobbyId) throws RemoteException {
        server.sendCommand(new ServerCommand.PickLobbyCommand(this.playerId, lobbyId));
    }

    public void joinGame(String nickname, Color color) throws RemoteException {
        this.server.sendCommand(new GameCommand.PickNameColorCommand(this.playerId, nickname, color));
    }

    public void pickCard(int id) {
        if (server == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        if (playerId == null) {
            throw new IllegalStateException("Player id is not set");
        }
        try {
            server.sendCommand(new GameCommand.PickCardCommand(this.playerId, id));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void placeTotem(int position) {
        if (server == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        if (playerId == null) {
            throw new IllegalStateException("Player id is not set");
        }
        try {
            server.sendCommand(new GameCommand.PlaceTotemCommand(this.playerId, position));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void endTurn() {
        if (server == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        if (playerId == null) {
            throw new IllegalStateException("Player id is not set");
        }
        try {
            server.sendCommand(new GameCommand.EndTurnCommand(this.playerId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToQueue(Message message) {
        try {
            this.messageQueue.put(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
