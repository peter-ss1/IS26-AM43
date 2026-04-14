package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.error.Error;

public class ClientController {
    private ClientModel localModel;
    private VirtualServer remoteModel;

    public ClientController(ClientModel localModel, VirtualServer remoteModel) {
        this.localModel = null;
        this.remoteModel = null;
    }

    public void chooseConnectionType(boolean rmi) {
        remoteModel.connect();
    }

    public void refreshLobbies() {
        remoteModel.sendCommand(new ServerCommand.FetchLobbiesCommand());
    }

    public void createLobby(String nickname, Color color, int numPlayers) {
        remoteModel.sendCommand(new ServerCommand.CreateLobbyCommand(nickname, color, numPlayers));
    }

    public void pickCard(int id, String nickname) {
        remoteModel.sendCommand(new GameCommand.PickCardCommand(this.localModel.getLobbyId(), id, nickname));
    }

    public void showError(Error error) {
    }
}
