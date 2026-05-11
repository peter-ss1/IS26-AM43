package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.view.gui.scenes.CustomScene;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.text.html.ImageView;

public class MainScene extends CustomScene {
    @FXML
    private StackPane root;
    @FXML
    private VBox menu;
    @FXML
    private ToggleButton fullscreen;
    @FXML
    private Button close;
    @FXML
    private HBox disconnectionMenu;
    @FXML
    private Button yes;
    @FXML
    private Button no;
    @FXML
    private HBox loadingScreen;

    public void switchScene(Node node) {
        this.root.getChildren().set(0, node);
        this.disconnectionMenu.setVisible(false);
        if (loadingScreen.isVisible()) hideLoadingScreen();
        this.root.getChildren().getFirst().setEffect(null);
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

    public void showDisconnectionMenu() {
        if (loadingScreen.isVisible()) hideLoadingScreen();
        this.root.getChildren().getFirst().setEffect(new GaussianBlur(15));
        this.disconnectionMenu.setVisible(true);
        this.yes.setDisable(false);
        this.no.setDisable(false);
    }

    @FXML
    public void onYesClicked() {
        this.yes.setDisable(true);
        this.no.setDisable(true);
        this.gui.submitTask(() -> this.controller.answerRejoin(true));
    }

    @FXML
    public void onNoClicked() {
        this.yes.setDisable(true);
        this.no.setDisable(true);
        this.gui.submitTask(() -> this.controller.answerRejoin(false));
    }

    public void showLoading() {
        showLoadingScreen();
        this.root.getChildren().getFirst().setEffect(new GaussianBlur(15));
    }

    private ParallelTransition loadingAnimation;

    private void startLoadingAnimation(Node icon) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(3), icon);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR); // Keep it steady

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.5), icon);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.15);
        pulse.setToY(1.15);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.setInterpolator(Interpolator.EASE_BOTH);

        Glow glow = new Glow(0.3);
        icon.setEffect(glow);
        Timeline glowCycle = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
                new KeyFrame(Duration.seconds(1), new KeyValue(glow.levelProperty(), 0.8))
        );
        glowCycle.setCycleCount(Animation.INDEFINITE);
        glowCycle.setAutoReverse(true);
        loadingAnimation = new ParallelTransition(rotate, pulse, glowCycle);
        loadingAnimation.play();
    }

    public void showLoadingScreen() {
        this.loadingScreen.setVisible(true);
        this.loadingScreen.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this.loadingScreen);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(e -> startLoadingAnimation(this.loadingScreen.getChildren().getFirst()));
        fadeIn.play();
    }
    public void hideLoadingScreen() {
        if (loadingAnimation != null) {
            loadingAnimation.stop();
        }

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this.loadingScreen);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            this.loadingScreen.setVisible(false);
            this.loadingScreen.getChildren().getFirst().setRotate(0);
        });
        fadeOut.play();
    }
}