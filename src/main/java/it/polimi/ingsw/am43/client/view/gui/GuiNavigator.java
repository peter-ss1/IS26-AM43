package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.view.ViewState;
import it.polimi.ingsw.am43.client.view.gui.scenes.CustomScene;
import it.polimi.ingsw.am43.controller.ClientController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;

import static it.polimi.ingsw.am43.client.view.gui.GuiSettings.CONNECTION_PATH;

public class GuiNavigator {
    private final Stage stage;
    private final GUI gui;
    private final ClientController controller;
    private CustomScene currentScene;
    private MainScene mainScene;

    public GuiNavigator(Stage stage, GUI gui, ClientController controller) {
        this.stage = stage;
        this.gui = gui;
        this.controller = controller;
    }

    public void init() {
        FXMLLoader loader = this.buildLoader("/it/polimi/ingsw/am43/fxml/main-view.fxml");
        Parent root = this.loadRoot(loader);
        this.mainScene = loader.getController();
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

    private Parent loadRoot(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
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
        this.mainScene.switchScene(root);
        this.currentScene = loader.getController();
        this.currentScene.setGui(this.gui);
        this.currentScene.setController(this.controller);
        this.currentScene.setUp();
    }

    public CustomScene getCurrentScene() {
        return this.currentScene;
    }

    public void showMenu() {
        this.mainScene.showMenu();
    }
}
