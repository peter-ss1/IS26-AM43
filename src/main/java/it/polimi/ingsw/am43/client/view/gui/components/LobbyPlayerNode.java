package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.model.enums.Color;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class LobbyPlayerNode extends VBox {
    private Label playerNameLabel;
    private String playerName;
    private boolean empty;

    public LobbyPlayerNode() {
        this.empty = true;
        this.playerName = "";
        this.getStyleClass().add("loginContainer");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setMinWidth(200);
        this.setMinHeight(200);
        this.setOpacity(0.5);
        String path = "/it/polimi/ingsw/am43/images/BLACKtotem.PNG";
        ImageView totemIcon = new ImageView(new Image(getClass().getResourceAsStream(path)));
        totemIcon.setFitWidth(100);
        totemIcon.setPreserveRatio(true);
        this.playerNameLabel = new Label("WAITING...");
        this.playerNameLabel.getStyleClass().add("loginLabel");
        this.getChildren().addAll(totemIcon, this.playerNameLabel);
    }

    public void activate(String nickname, Color color, boolean disconnected, boolean isOwnPlayer) {
        this.empty = false;
        this.playerName = nickname;
        this.setOpacity(disconnected ? 0.5 : 1);
        this.getChildren().clear();
        if (isOwnPlayer) {
            this.getStyleClass().remove("loginContainer");
            this.getStyleClass().add("ownLobbyPlayer");
        }
        String path = "/it/polimi/ingsw/am43/images/" + color.name() + "totem.PNG";
        ImageView totemIcon = new ImageView(new Image(getClass().getResourceAsStream(path)));
        totemIcon.setFitWidth(100);
        totemIcon.setPreserveRatio(true);
        this.playerNameLabel = new Label(disconnected ? "RECONNECTING..." : nickname);
        this.playerNameLabel.getStyleClass().add(isOwnPlayer? "ownLobbyLabel" : "loginLabel");
        this.getChildren().addAll(totemIcon, this.playerNameLabel);
        animateTransition(totemIcon);
    }

    public void animateTransition(Node totem) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(300), this);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);

        totem.setOpacity(0);
        totem.setScaleX(0.8);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), totem);
        fadeIn.setDelay(Duration.millis(200));
        fadeIn.setToValue(1.0);
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(500), totem);
        scaleIn.setDelay(Duration.millis(200));
        scaleIn.setToX(1.0);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(pulse, fadeIn, scaleIn).play();
    }

    public boolean isEmpty() {
        return  empty;
    }

    public String getNickname() {
        return this.playerNameLabel.getText();
    }

    public void update(boolean disconnected) {
        this.playerNameLabel.setText(disconnected? "RECONNECTING..." : this.playerName);
        this.setOpacity(disconnected ? 0.5 : 1);
    }

    public void deactivate() {
        this.empty = true;
        this.playerName = "";
        this.getStyleClass().remove("ownLobbyPlayer");
        this.getStyleClass().add("loginContainer");
        this.setOpacity(0.5);
        this.getChildren().clear();
        String path = "/it/polimi/ingsw/am43/images/BLACKtotem.PNG";
        ImageView totemIcon = new ImageView(new Image(getClass().getResourceAsStream(path)));
        totemIcon.setFitWidth(100);
        totemIcon.setPreserveRatio(true);
        this.playerNameLabel = new Label("WAITING...");
        this.playerNameLabel.getStyleClass().add("loginLabel");
        this.getChildren().addAll(totemIcon, this.playerNameLabel);
        this.animateTransition(totemIcon);
    }
}