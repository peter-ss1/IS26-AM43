package it.polimi.ingsw.am43.client.view.gui.SceneController;

import it.polimi.ingsw.am43.client.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class ConnectionViewController {
    @FXML
    private TextField ipField;
    @FXML
    private RadioButton rmiRadio;
    @FXML
    private Label errorLabel;
    @FXML
    private Button connectButton;

    private GUI gui;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    @FXML
    private void onConnectClicked() {
        if (gui == null) {
            showError("GUI not initialized.");
            return;
        }
        if (connectButton != null) {
            connectButton.setDisable(true);
        }

        String ip = ipField.getText() == null ? "" : ipField.getText().trim();
        if (ip.isEmpty()) {
            ip = "localhost";
        } else if (!isValidHostOrIp(ip)) {
            showError("Invalid host. Use localhost, a valid IPv4, or a hostname.");
            if (connectButton != null) {
                connectButton.setDisable(false);
            }
            return;
        }
        boolean isRmi = rmiRadio.isSelected();
        showError("Connecting...");
        gui.connectAsync(ip, isRmi, message -> {
            showError(message);
            if (connectButton != null) {
                connectButton.setDisable(false);
            }
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private boolean isValidHostOrIp(String value) {
        if (value.equalsIgnoreCase("localhost")) {
            return true;
        }
        if (isValidIpv4(value)) {
            return true;
        }
        return value.matches("^(?=.{1,253}$)(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.(?!-)[A-Za-z0-9-]{1,63}(?<!-))*$");
    }

    private boolean isValidIpv4(String value) {
        String[] parts = value.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            if (!part.matches("\\d{1,3}")) {
                return false;
            }
            int octet = Integer.parseInt(part);
            if (octet < 0 || octet > 255) {
                return false;
            }
        }
        return true;
    }
}
