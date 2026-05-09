package it.polimi.ingsw.am43.client.view.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainScene {
    @FXML
    private StackPane root;
    @FXML
    private VBox menu;
    @FXML
    private ToggleButton fullscreen;
    @FXML
    private Button close;

    public void switchScene(Node node) {
        this.root.getChildren().set(0, node);
        this.menu.toFront();
    }

    @FXML
    public void onFullscreenToggled() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setFullScreen(this.fullscreen.isSelected());
    }

    @FXML
    public void onCloseClicked() {
        this.menu.setVisible(false);
    }

    public void showMenu() {
        this.menu.setVisible(true);
    }
}