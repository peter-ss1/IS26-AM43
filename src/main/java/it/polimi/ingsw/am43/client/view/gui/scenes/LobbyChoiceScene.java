package it.polimi.ingsw.am43.client.view.gui.scenes;

import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.client.view.gui.GUI;
import it.polimi.ingsw.am43.model.enums.Color;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class LobbyChoiceScene extends CustomScene {
    @FXML
    private TableView<LobbyInfo> lobbiesTable;
    @FXML
    private TableColumn<LobbyInfo, Integer> lobbyIdColumn;
    @FXML
    private TableColumn<LobbyInfo, Integer> currentPlayersColumn;
    @FXML
    private TableColumn<LobbyInfo, Integer> maxPlayersColumn;
    @FXML
    private TextField joinLobbyIdField;
    @FXML
    private TextField nicknameField;
    @FXML
    private ComboBox<Color> colorCombo;
    @FXML
    private Spinner<Integer> numPlayersSpinner;
    @FXML
    private Label errorLabel;
    @FXML
    private Button joinSelectedLobbyButton;

    @FXML
    private void initialize() {
        lobbyIdColumn.setCellValueFactory(new PropertyValueFactory<>("lobbyId"));
        currentPlayersColumn.setCellValueFactory(new PropertyValueFactory<>("currentPlayers"));
        maxPlayersColumn.setCellValueFactory(new PropertyValueFactory<>("numPlayers"));
        colorCombo.setItems(FXCollections.observableArrayList(Color.values()));
        numPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 2));
        joinSelectedLobbyButton.setDisable(true);
        lobbiesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) ->
                joinSelectedLobbyButton.setDisable(newSel == null));
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void refreshFromModel() {
        if (gui == null) {
            return;
        }
        lobbiesTable.setItems(FXCollections.observableArrayList(gui.getLocalModel().getLobbies()));
    }

    @FXML
    private void onRefreshClicked() {
        if (gui == null) {
            showError("GUI not initialized.");
            return;
        }
        showError("Refreshing lobbies...");
        this.gui.submitTask(() -> this.controller.refreshLobbies());
    }

    @FXML
    private void onJoinClicked() {
        if (gui == null) {
            showError("GUI not initialized.");
            return;
        }
        LobbyInfo selectedLobby = lobbiesTable.getSelectionModel().getSelectedItem();
        if (selectedLobby == null) {
            showError("Select a lobby.");
            return;
        }
        this.gui.submitTask(() -> this.controller.joinLobby(selectedLobby.getLobbyId()));
        errorLabel.setText("");
    }

    @FXML
    private void onCreateClicked() {
        if (gui == null) {
            showError("GUI not initialized.");
            return;
        }
        String nickname = nicknameField.getText() == null ? "" : nicknameField.getText().trim();
        if (!nickname.matches("^[a-zA-Z0-9]{3,12}$")) {
            showError("Nickname must be 3-12 alphanumeric chars.");
            return;
        }
        Color color = colorCombo.getValue();
        if (color == null) {
            showError("Please choose a color.");
            return;
        }
        int numPlayers = numPlayersSpinner.getValue();
        this.gui.submitTask(() -> this.controller.createLobby(nickname, color, numPlayers));
        errorLabel.setText("");
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }
}
