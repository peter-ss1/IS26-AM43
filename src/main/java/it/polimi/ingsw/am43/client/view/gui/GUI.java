package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.client.view.ViewState;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.enums.Color;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GUI implements UI {
    private final GuiApplication app;
    private final ClientModel localModel;
    private final ClientController controller;
    private final GuiNavigator navigator;
    private ViewState state;

    public GUI(GuiApplication application, Stage stage) {
        this.app = application;
        this.localModel = new ClientModel(this);
        this.controller = new ClientController(this, this.localModel);
        this.navigator = new GuiNavigator(stage, this, this.controller);
        this.navigator.init();
        this.state = ViewState.CONNECTION;
    }

    public ClientModel getLocalModel() {
        return localModel;
    }

    public void connectAsync(String serverIp, boolean rmi, Consumer<String> onError) {
        new Thread(() -> {
            try {
                this.controller.chooseConnectionType(serverIp, rmi);
                this.state = ViewState.LOBBY_CHOICE;
                Platform.runLater(() -> {
                    this.navigator.showScene(ViewState.LOBBY_CHOICE);
                });
                this.controller.refreshLobbies();
            } catch (IOException | NotBoundException e) {
                Platform.runLater(() -> onError.accept("Could not reach server at " + serverIp + ". " + e.getMessage()));
            }
        }).start();
    }

    public void submitTask(Runnable task) {
        this.app.runAsync(task);
    }

    public void refreshLobbies() {
        new Thread(this.controller::refreshLobbies).start();
    }

    public void createLobby(String nickname, Color color, int numPlayers) {
        new Thread(() -> this.controller.createLobby(nickname, color, numPlayers)).start();
    }

    public void joinLobby(int lobbyId) {
        new Thread(() -> this.controller.joinLobby(lobbyId)).start();
    }

    public void joinGame(String nickname, Color color) {
        new Thread(() -> this.controller.joinGame(nickname, color)).start();
    }

    @Override
    public void showAvailableLobbies() {
        if (this.state != ViewState.LOBBY_CHOICE) return;
        Platform.runLater(() -> this.navigator.getCurrentScene().refreshFromModel());
    }

    @Override
    public void enterLobby() {
        this.state = ViewState.IN_LOBBY;
        Platform.runLater(() -> {
            this.navigator.showScene(this.state);
            this.navigator.getCurrentScene().refreshFromModel();
        });
    }

    @Override
    public void showNewPlayer() {
        if (this.state != ViewState.IN_LOBBY) return;
        Platform.runLater(() -> navigator.getCurrentScene().refreshFromModel());
    }

    @Override
    public void showGameStart() {
        //this.state = ViewState.IN_GAME;
        Platform.runLater(() -> {
            //this.navigator.showScene(this.state);
            this.navigator.getCurrentScene().showInfo("Game started.");
        });
    }

    @Override
    public void handleLobbyChoiceError(String message, boolean creation) {
        this.state = ViewState.LOBBY_CHOICE;
        Platform.runLater(() -> {
            this.navigator.showScene(this.state);
                String operation = creation ? "creation" : "join";
                this.navigator.getCurrentScene().showError("Lobby " + operation + " failed: " + message);
                this.navigator.getCurrentScene().refreshFromModel();
        });
    }

    @Override
    public void handleLobbyJoinError(String message) {
        Platform.runLater(() -> navigator.getCurrentScene().showError("Cannot join game: " + message));
    }

    @Override
    public void showGameError(String error) {
    }

    @Override
    public void showNewCurrPlayer() {
    }

    @Override
    public void showTotemPlaced(String nickname, int position) {
    }

    @Override
    public void showCardPicked(String nickname, int cardId) {
    }

    @Override
    public void showBuildingAcquisition(String nickname, int cost) {
    }

    @Override
    public void showHunterEffect(String nickname, int food) {
    }

    @Override
    public void showBuildingEffect(String nickname, int bonus, String resource) {
    }

    @Override
    public void showHuntEvent(Map<String, List<Integer>> effects) {
    }

    @Override
    public void showPaintingEvent(Map<String, Integer> effects) {
    }

    @Override
    public void showSustenanceEvent(Map<String, List<Integer>> effects) {
    }

    @Override
    public void showRitualEvent(Map<String, Integer> effects) {
    }

    @Override
    public void showGameEnd() {
    }

    @Override
    public void showOrderModifier(String nickname, int modifier, boolean prestige) {
    }

    @Override
    public void showFoodOffer(String nickname) {
    }
}
