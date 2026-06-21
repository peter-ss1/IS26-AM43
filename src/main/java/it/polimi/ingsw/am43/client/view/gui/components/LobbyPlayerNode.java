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
/**
 * A custom JavaFX UI component representing a player slot in the game lobby.
 * Can transition between an empty waiting state and an active player display.
 */
public class LobbyPlayerNode extends VBox {
    private Label playerNameLabel;
    private String playerName;
    private boolean empty;

    /**
     * Constructs an empty lobby slot initialized to a "WAITING..." state
     * with a default black placeholder totem.
     */
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

    /**
     * Populates and activates the slot with the player's details, styling, and entrance animation.
     *
     * @param nickname The username of the player.
     * @param color The color of the player's selected totem.
     * @param disconnected True if the player is currently disconnected.
     * @param isOwnPlayer True if this slot belongs to the local client.
     */
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

    /**
     * Triggers a parallel animation playing a container pulse alongside a totem fade-and-scale-in.
     *
     * @param totem The totem image node to animate.
     */
    private void animateTransition(Node totem) {
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

    /**
     * Checks if the lobby slot is currently unoccupied.
     *
     * @return True if empty, false if assigned to a player.
     */
    public boolean isEmpty() {
        return  empty;
    }

    /**
     * Retrieves the current text displayed within the player name label.
     *
     * @return The text value of the name label.
     */
    public String getNickname() {
        return this.playerNameLabel.getText();
    }

    /**
     * Updates the slot's labels and opacity to reflect a player's connection status.
     *
     * @param disconnected True to show reconnection status, false for active state.
     */
    public void update(boolean disconnected) {
        this.playerNameLabel.setText(disconnected? "RECONNECTING..." : this.playerName);
        this.setOpacity(disconnected ? 0.5 : 1);
    }

    /**
     * Resets the slot back to its initial unoccupied waiting state with an animation.
     */
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