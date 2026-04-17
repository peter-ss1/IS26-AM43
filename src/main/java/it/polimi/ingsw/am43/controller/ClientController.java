package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;
import it.polimi.ingsw.am43.network.rmi.ClientRMI;
import it.polimi.ingsw.am43.network.rmi.VirtualServerRMI;
import it.polimi.ingsw.am43.network.socket.client.SocketClient;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientController {
    private UI ui;
    private ClientModel clientModel;
    private ClientRMI clientRMI;
    private ClientModel localModel;
    private VirtualServer remoteModel;
    private final BlockingQueue<Message> messageQueue;
    private Error lastError;
    private UUID playerId;

    public ClientController() {
        this.ui = null;
        this.localModel = null;
        this.remoteModel = null;
        this.lastError = null;
        this.playerId = UUID.randomUUID();
        this.messageQueue = new LinkedBlockingQueue<>();
        new Thread(this::executor).start();
    }

    public void setUi(UI ui) {
        this.ui = ui;
    }
    private void executor() {
        while (true) {
            try {
                Message message = messageQueue.take();
                message.execute(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public ClientModel getLocalModel() {
        return localModel;
    }

    public void setLocalModel(ClientModel localModel) {
        this.localModel = localModel;
    }

    public VirtualServer getRemoteModel() {
        return remoteModel;
    }

    public void setRemoteModel(VirtualServer remoteModel) {
        this.remoteModel = remoteModel;
    }

    public Error getLastError() {
        return lastError;
    }

    public void chooseConnectionType(boolean rmi) throws IOException {
        if (rmi) {
            try {
                Registry registry = LocateRegistry.getRegistry(InetAddress.getLocalHost().getHostAddress(), 1099);
                this.remoteModel = (VirtualServerRMI) registry.lookup("MesosServer");
                ((VirtualServerRMI) this.remoteModel).connect(this.playerId, new ClientRMI(this));

                System.out.println("Successfully connected to RMI server.");

            } catch (Exception e) {
                System.err.println("Client Error: Could not connect to RMI server.");
                e.printStackTrace();
            }
        }
        else {
            Socket serverSocket;
            try {
                serverSocket = new Socket(InetAddress.getLocalHost().getHostAddress(), 8080);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            InputStreamReader socketRx = new InputStreamReader(serverSocket.getInputStream());
            OutputStreamWriter socketTx = new OutputStreamWriter(serverSocket.getOutputStream());

            new SocketClient(new BufferedReader(socketRx), new BufferedWriter(socketTx), this).run();
            this.remoteModel.sendCommand(new ServerCommand.RegisterCommand(this.playerId));
        }
    }

    public void refreshLobbies() {
        try {
            remoteModel.sendCommand(new ServerCommand.FetchLobbiesCommand(this.playerId));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void createLobby(String nickname, Color color, int numPlayers) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        try {
            remoteModel.sendCommand(new ServerCommand.CreateLobbyCommand(this.playerId, nickname, color, numPlayers));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void pickLobby(int lobbyId) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        try {
            remoteModel.sendCommand(new ServerCommand.PickLobbyCommand(this.playerId , lobbyId));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /*public void addPlayer(int lobbyId, String nickname, Color color) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        remoteModel.sendCommand(new ServerCommand.AddPlayerCommand(this.playerId, lobbyId, nickname, color));
    }*/

    public void pickCard(int id, String nickname) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        if (playerId == null) {
            throw new IllegalStateException("Player id is not set");
        }
        try {
            remoteModel.sendCommand(new GameCommand.PickCardCommand(playerId, id, nickname));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleUpdate(Update update) {
        if (update == null) {
            throw new IllegalArgumentException("Update cannot be null");
        }
        update.execute(this);
        if (localModel != null) {
            update.execute(localModel);
        }
    }

    public void showError(Error error) {
        this.lastError = error;
    }

    public void placeTotem(int position, String nickname) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        if (playerId == null) {
            throw new IllegalStateException("Player id is not set");
        }
        try {
            remoteModel.sendCommand(new GameCommand.PlaceTotemCommand(playerId, position, nickname));
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void endTurn(String nickname) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        if (playerId == null) {
            throw new IllegalStateException("Player id is not set");
        }
        try {
            remoteModel.sendCommand(new GameCommand.EndTurnCommand(playerId, nickname));
        } catch (java.rmi.RemoteException e) {
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

    public void sendString(String message) throws RemoteException {
        this.remoteModel.sendCommand(new ServerCommand.StringCommand(this.playerId, message));
    }

    public void showString(String message) {
        this.ui.print(message);
    }
}
