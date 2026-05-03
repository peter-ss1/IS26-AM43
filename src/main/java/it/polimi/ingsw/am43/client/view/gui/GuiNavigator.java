package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.view.gui.SceneController.ConnectionViewController;
import it.polimi.ingsw.am43.client.view.gui.SceneController.InLobbyViewController;
import it.polimi.ingsw.am43.client.view.gui.SceneController.LobbyChoiceViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiNavigator {
    private final Stage stage;
    private final GUI gui;

    private LobbyChoiceViewController lobbyChoiceController;
    private InLobbyViewController inLobbyController;

    public GuiNavigator(Stage stage, GUI gui) {
        this.stage = stage;
        this.gui = gui;
    }

    public void showConnectionView() {
        FXMLLoader loader = buildLoader("/it/polimi/ingsw/am43/fxml/connection-view.fxml");
        Scene scene = loadScene(loader);
        ConnectionViewController controller = loader.getController();
        controller.setGui(gui);
        stage.setTitle("Mesos - Connection");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        stage.setMinWidth(900);
        stage.setMinHeight(600);
    }

    public void showLobbyChoiceView() {
        FXMLLoader loader = buildLoader("/it/polimi/ingsw/am43/fxml/lobby-choice-view.fxml");
        Scene scene = loadScene(loader);
        LobbyChoiceViewController controller = loader.getController();
        controller.setGui(gui);
        this.lobbyChoiceController = controller;
        this.inLobbyController = null;
        stage.setTitle("Mesos - Lobby Choice");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        controller.refreshFromModel();
    }

    public void showInLobbyView() {
        FXMLLoader loader = buildLoader("/it/polimi/ingsw/am43/fxml/in-lobby-view.fxml");
        Scene scene = loadScene(loader);
        InLobbyViewController controller = loader.getController();
        controller.setGui(gui);
        this.inLobbyController = controller;
        this.lobbyChoiceController = null;
        stage.setTitle("Mesos - In Lobby");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        controller.refreshFromModel();
    }

    public LobbyChoiceViewController getLobbyChoiceController() {
        return lobbyChoiceController;
    }

    public InLobbyViewController getInLobbyController() {
        return inLobbyController;
    }

    private FXMLLoader buildLoader(String resourcePath) {
        return new FXMLLoader(getClass().getResource(resourcePath));
    }

    private Scene loadScene(FXMLLoader loader) {
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load FXML.", e);
        }
        return new Scene(root);
    }
}
