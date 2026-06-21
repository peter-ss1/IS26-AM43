package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.view.ViewState;
import it.polimi.ingsw.am43.client.view.gui.scenes.CustomScene;
import it.polimi.ingsw.am43.client.view.gui.scenes.MainScene;
import it.polimi.ingsw.am43.controller.ClientController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;

import static it.polimi.ingsw.am43.client.view.gui.GuiSettings.CONNECTION_PATH;

/**
 * Handles window initialization and transitions between FXML-based scenes.
 */
public class GuiNavigator {
    private final Stage stage;
    private final GUI gui;
    private final ClientController controller;
    private CustomScene currentScene;
    private MainScene mainScene;

    /**
     * Constructs a navigator tied to the primary application stage and controller.
     */
    public GuiNavigator(Stage stage, GUI gui, ClientController controller) {
        this.stage = stage;
        this.gui = gui;
        this.controller = controller;
    }

    /**
     * Configures the primary window properties and initializes the connection scene.
     */
    public void init() {
        FXMLLoader loader = this.buildLoader("/it/polimi/ingsw/am43/fxml/main-view.fxml");
        Parent root = this.loadRoot(loader);
        this.mainScene = loader.getController();
        this.mainScene.setGui(this.gui);
        this.mainScene.setController(this.controller);
        Scene scene = new Scene(root, 1280, 720);
        this.stage.setScene(scene);
        this.stage.setTitle("MESOS");
        this.stage.setResizable(false);
        this.stage.centerOnScreen();
        this.stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        this.stage.setFullScreenExitHint("");
        this.stage.getIcons().add(new Image(getClass().getResourceAsStream("/it/polimi/ingsw/am43/images/REDtotem.PNG")));
        loader = this.buildLoader(CONNECTION_PATH);
        this.mainScene.switchScene(this.loadRoot(loader));
        this.currentScene = loader.getController();
        this.currentScene.setGui(this.gui);
        this.currentScene.setController(this.controller);
        this.stage.show();
    }

    /**
     * Safely loads an FXML root element, wrapping exceptions in explanation messages.
     */
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

    /**
     * Loads required scene based on state and updates the window with the swapped scene.
     */
    public void showScene(ViewState scene) {
        FXMLLoader loader = this.buildLoader(GuiSettings.getPath(scene));
        Parent root = this.loadRoot(loader);
        this.mainScene.switchScene(root);
        this.currentScene = loader.getController();
        this.currentScene.setGui(this.gui);
        this.currentScene.setController(this.controller);
        this.currentScene.setUp();
    }

    /**
     * Gets the currently active scene controller.
     */
    public CustomScene getCurrentScene() {
        return this.currentScene;
    }

    /**
     * Directs the main scene controller to display the game menu overlay panel.
     */
    public void showMenu() {
        this.mainScene.showMenu();
    }

    /**
     * Directs the main scene controller to display the recovery choice overlay screen.
     */
    public void showDisconnectionMenu() {
        this.mainScene.showDisconnectionMenu();
    }

    /**
     * Directs the main scene controller to display the loading overlay screen.
     */
    public void showLoading() {
        this.mainScene.showLoading();
    }
}
