package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.view.ViewState;
import it.polimi.ingsw.am43.client.view.gui.scenes.ConnectionScene;
import it.polimi.ingsw.am43.client.view.gui.scenes.CustomScene;
import it.polimi.ingsw.am43.client.view.gui.scenes.InLobbyScene;
import it.polimi.ingsw.am43.client.view.gui.scenes.LobbyChoiceScene;
import it.polimi.ingsw.am43.controller.ClientController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static it.polimi.ingsw.am43.client.view.gui.GuiSettings.CONNECTION_PATH;

public class GuiNavigator {
    private final Stage stage;
    private final GUI gui;
    private final ClientController controller;

    private CustomScene currentScene;

    public GuiNavigator(Stage stage, GUI gui, ClientController controller) {
        this.stage = stage;
        this.gui = gui;
        this.controller = controller;
    }

    public void init() {
        FXMLLoader loader = this.buildLoader(CONNECTION_PATH);
        Parent root = this.loadRoot(loader);
        this.currentScene = loader.getController();
        this.currentScene.setGui(this.gui);
        this.currentScene.setController(this.controller);
        Scene scene = new Scene(root, 1280, 720);
        this.stage.setScene(scene);
        this.stage.setTitle("Mesos");
        this.stage.setResizable(false);
        this.stage.centerOnScreen();
        this.stage.show();
    }

    private Parent loadRoot(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load FXML file: " + e.getMessage());
        } catch (NullPointerException e) {
            throw new RuntimeException("FXML file not found. " + e.getMessage());
        }
    }

    private FXMLLoader buildLoader(String resourcePath) {
        return new FXMLLoader(getClass().getResource(resourcePath));
    }

    public void showScene(ViewState scene) {
        FXMLLoader loader = this.buildLoader(GuiSettings.getPath(scene));
        Parent root = this.loadRoot(loader);
        this.currentScene = loader.getController();
        this.currentScene.setGui(this.gui);
        this.currentScene.setController(this.controller);
        this.stage.getScene().setRoot(root);
    }

    public CustomScene getCurrentScene() {
        return this.currentScene;
    }
}
