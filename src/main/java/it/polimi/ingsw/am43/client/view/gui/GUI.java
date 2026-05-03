package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.view.UI;
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
    private final ClientModel localModel;
    private final ClientController controller;
    private GuiNavigator navigator;

    public GUI() {
        this.localModel = new ClientModel(this);
        this.controller = new ClientController(this, this.localModel);
    }

    public void initialize(Stage stage) {
        this.navigator = new GuiNavigator(stage, this);
        this.navigator.showConnectionView();
    }

    public ClientModel getLocalModel() {
        return localModel;
    }

    public void connect(String serverIp, boolean rmi) throws IOException, NotBoundException {
        connectAsync(serverIp, rmi, this::handleConnectionError);
    }

    public void connectAsync(String serverIp, boolean rmi, Consumer<String> onError) {
        new Thread(() -> {
            try {
                this.controller.chooseConnectionType(serverIp, rmi);
                Platform.runLater(() -> {
                    this.navigator.showLobbyChoiceView();
                });
                this.controller.refreshLobbies();
            } catch (IOException | NotBoundException e) {
                Platform.runLater(() -> onError.accept("Could not reach server at " + serverIp + ". " + e.getMessage()));
            }
        }).start();
    }

    public void refreshLobbies() {
        new Thread(() -> this.controller.refreshLobbies()).start();
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

    private void handleConnectionError(String message) {
        if (navigator.getLobbyChoiceController() != null) {
            navigator.getLobbyChoiceController().showError(message);
            return;
        }
        if (navigator.getInLobbyController() != null) {
            navigator.getInLobbyController().showError(message);
            return;
        }
        System.err.println(message);
    }

    @Override
    public void showAvailableLobbies() {
        Platform.runLater(() -> {
            if (navigator.getLobbyChoiceController() != null) {
                navigator.getLobbyChoiceController().refreshFromModel();
            }
        });
    }

    @Override
    public void enterLobby() {
        Platform.runLater(() -> {
            this.navigator.showInLobbyView();
            if (navigator.getInLobbyController() != null) {
                navigator.getInLobbyController().refreshFromModel();
            }
        });
    }

    @Override
    public void showNewPlayer() {
        Platform.runLater(() -> {
            if (navigator.getInLobbyController() != null) {
                navigator.getInLobbyController().refreshFromModel();
            }
        });
    }

    @Override
    public void showGameStart() {
        Platform.runLater(() -> {
            if (navigator.getInLobbyController() != null) {
                navigator.getInLobbyController().showInfo("Game started.");
            }
        });
    }

    @Override
    public void handleLobbyChoiceError(String message, boolean creation) {
        Platform.runLater(() -> {
            this.navigator.showLobbyChoiceView();
            if (navigator.getLobbyChoiceController() != null) {
                String operation = creation ? "creation" : "join";
                navigator.getLobbyChoiceController().showError("Lobby " + operation + " failed: " + message);
                navigator.getLobbyChoiceController().refreshFromModel();
            }
        });
    }

    @Override
    public void handleLobbyJoinError(String message) {
        Platform.runLater(() -> {
            if (navigator.getInLobbyController() != null) {
                navigator.getInLobbyController().showError("Cannot join game: " + message);
            }
        });
    }

    @Override
    public void showGameError(String error) {
        Platform.runLater(() -> {
            if (navigator.getInLobbyController() != null) {
                navigator.getInLobbyController().showError(error);
            }
            if (navigator.getLobbyChoiceController() != null) {
                navigator.getLobbyChoiceController().showError(error);
            }
        });
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
