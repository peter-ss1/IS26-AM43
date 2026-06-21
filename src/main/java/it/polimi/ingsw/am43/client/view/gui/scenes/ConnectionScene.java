package it.polimi.ingsw.am43.client.view.gui.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.*;
/**
 * Scene controller for managing the initial server connection screen.
 * Handles the validation of the server IP address and communication protocol selection.
 */
public class ConnectionScene extends CustomScene {
    @FXML
    private TextField ipField;
    @FXML
    private ComboBox<String> connectionType;
    @FXML
    private Button connectButton;

    /**
     * Validates user input and initiates the network connection setup.
     * Disables UI controls during validation, defaults empty IPs to "localhost",
     * and dispatches an asynchronous connection task if inputs are valid.
     */
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
            String finalIp = ip;
            this.gui.submitTask(() -> this.controller.chooseConnectionType(finalIp, selected.equals("RMI")));
        } else {
            this.connectButton.setDisable(false);
            this.ipField.setDisable(false);
            this.connectionType.setDisable(false);
        }
    }
}
