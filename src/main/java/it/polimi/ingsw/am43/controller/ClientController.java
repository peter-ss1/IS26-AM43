package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;

public class ClientController {
    private ClientModel localModel;
    private VirtualServer remoteModel;
    private Error lastError;

    public ClientController(ClientModel localModel, VirtualServer remoteModel) {
        this.localModel = localModel;
        this.remoteModel = remoteModel;
        this.lastError = null;

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

    public void chooseConnectionType(boolean rmi) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        remoteModel.connect();
    }

    public void refreshLobbies() {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        remoteModel.sendCommand(new ServerCommand.FetchLobbiesCommand());
    }

    public void createLobby(String nickname, Color color, int numPlayers) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        remoteModel.sendCommand(new ServerCommand.CreateLobbyCommand(nickname, color, numPlayers));
    }

    public void pickLobby(int lobbyId) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        remoteModel.sendCommand(new ServerCommand.PickLobbyCommand(lobbyId));
    }

    public void addPlayer(int lobbyId, String nickname, Color color) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        remoteModel.sendCommand(new ServerCommand.AddPlayerCommand(lobbyId, nickname, color));
    }

    public void pickCard(int id, String nickname) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        remoteModel.sendCommand(new GameCommand.PickCardCommand(localModel.getLobbyId(), id, nickname));
    }

    public void handleUpdate(Update update) {
        if (update == null) {
            throw new IllegalArgumentException("Update cannot be null");
        }
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
        remoteModel.sendCommand(new GameCommand.PlaceTotemCommand(localModel.getLobbyId(), position, nickname));
    }

    public void endTurn(String nickname) {
        if (remoteModel == null) {
            throw new IllegalStateException("Remote model is not set");
        }
        if (localModel == null) {
            throw new IllegalStateException("Local model is not set");
        }
        remoteModel.sendCommand(new GameCommand.EndTurnCommand(localModel.getLobbyId(), nickname));
    }


}
