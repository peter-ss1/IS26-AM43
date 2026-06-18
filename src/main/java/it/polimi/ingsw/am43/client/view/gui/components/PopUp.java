package it.polimi.ingsw.am43.client.view.gui.components;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class PopUp {
    private final Stage parent;

    public PopUp(Stage parent) {
        this.parent = parent;
    }

    public void show(String message) {
        Stage popUpStage = new Stage();
        popUpStage.initOwner(parent);
        popUpStage.setResizable(false);
        popUpStage.initStyle(StageStyle.TRANSPARENT);
        Text text = new Text(message);
        text.getStyleClass().add("popup-text");
        StackPane root = new StackPane(text);
        root.getStyleClass().add("popup-container");
        root.setOpacity(0);
        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(PopUp.class.getResource("/it/polimi/ingsw/am43/style.css").toExternalForm());
        popUpStage.setScene(scene);
        popUpStage.setOpacity(0);
        popUpStage.show();
        double centerX = this.parent.getX() + (this.parent.getWidth() / 2) - (popUpStage.getWidth() / 2);
        double bottomY = this.parent.getY() + this.parent.getHeight() - popUpStage.getHeight() - 50;
        popUpStage.setX(centerX);
        popUpStage.setY(bottomY);
        popUpStage.setOpacity(1);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.9);
        fadeIn.setOnFinished(e -> {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
                    fadeOut.setFromValue(0.9);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(ae -> popUpStage.close());
                    fadeOut.play();
                } catch (InterruptedException ex) { ex.printStackTrace(); }
            }).start();
        });
        fadeIn.play();
    }
}