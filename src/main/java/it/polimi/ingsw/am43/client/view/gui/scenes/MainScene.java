package it.polimi.ingsw.am43.client.view.gui.scenes;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    @FXML
    private ImageView ruleImg;

    private int carouselIndex;

    @FXML
    private void initialize() {
        this.root.getStylesheets().add(getClass().getResource("/it/polimi/ingsw/am43/style.css").toExternalForm());
    }

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
        this.root.getChildren().getFirst().setEffect(null);
        this.menu.setVisible(false);
    }

    public void showMenu() {
        this.menu.setVisible(true);
        this.root.getChildren().getFirst().setEffect(new GaussianBlur(15));
        this.showImage(1);
    }

    private void showImage(int index) {
        if (index == 0 || index == 9) index = 1;
        this.carouselIndex = index;
        Image image = new Image(getClass().getResourceAsStream(String.format("/it/polimi/ingsw/am43/images/rules/rule%d.jpg", this.carouselIndex)));
        this.ruleImg.setImage(image);
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

    @FXML
    public void onPreviousClicked() {
        this.showImage(carouselIndex - 1);
    }
    @FXML
    public void onNextClicked() {
        this.showImage(carouselIndex + 1);
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