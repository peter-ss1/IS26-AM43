package it.polimi.ingsw.am43.client.view.gui.scenes;

import it.polimi.ingsw.am43.client.view.ViewState;
import it.polimi.ingsw.am43.client.view.gui.GUI;
import it.polimi.ingsw.am43.client.view.gui.PopUp;
import it.polimi.ingsw.am43.controller.ClientController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.rmi.NotBoundException;

public class ConnectionScene extends CustomScene {
    @FXML
    private TextField ipField;
    @FXML
    private ComboBox<String> connectionType;
    @FXML
    private Button connectButton;

    @FXML
    private void onConnectClicked() {
        boolean valid = true;
        this.connectButton.setDisable(true);
        this.ipField.setDisable(true);
        this.connectionType.setDisable(true);
        String ip = this.ipField.getText() == null ? "" : this.ipField.getText().trim();
        if (ip.isEmpty()) {
            this.ipField.setPromptText("localhost");
            ip = "localhost";
        } else if (!ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
            this.ipField.setText("");
            this.ipField.setPromptText("Invalid IP Address");
            valid = false;
        }
        String selected = this.connectionType.getValue();
        if (selected == null) {
            this.connectionType.setPromptText("Choose one");
            valid = false;
        }
        if (valid) {
            this.connectButton.setText("Connecting...");
            this.gui.connectAsync(ip, selected.equals("RMI"), message -> {
                this.gui.showPopUp(message);
                this.connectButton.setText("Connect to Server");
                this.connectButton.setDisable(false);
                this.ipField.setDisable(false);
                this.connectionType.setDisable(false);
            });
        } else {
            this.connectButton.setDisable(false);
            this.ipField.setDisable(false);
            this.connectionType.setDisable(false);
        }
    }
}
