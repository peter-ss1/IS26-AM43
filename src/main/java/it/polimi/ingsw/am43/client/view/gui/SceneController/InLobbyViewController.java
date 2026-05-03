package it.polimi.ingsw.am43.client.view.gui.SceneController;

import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.client.view.gui.GUI;
import it.polimi.ingsw.am43.model.enums.Color;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;

public class InLobbyViewController {
    @FXML
    private Label lobbyIdLabel;
    @FXML
    private Label lobbyStatusLabel;
    @FXML
    private Label waitingLabel;
    @FXML
    private Label infoLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private TableView<ClientPlayer> playersTable;
    @FXML
    private TableColumn<ClientPlayer, String> nicknameColumn;
    @FXML
    private TableColumn<ClientPlayer, String> colorColumn;
    @FXML
    private TextField nicknameField;
    @FXML
    private ComboBox<Color> colorCombo;
    @FXML
    private Label joinFormTitle;
    @FXML
    private GridPane joinFormGrid;
    @FXML
    private javafx.scene.control.Button joinGameButton;

    private GUI gui;

    @FXML
    private void initialize() {
        nicknameColumn.setCellValueFactory(data -> {
            String nickname = data.getValue().getNickname();
            if (gui != null && gui.getLocalModel().getOwnPlayer() != null
                    && gui.getLocalModel().getOwnPlayer().getNickname().equals(nickname)) {
                nickname += " (YOU)";
            }
            return new SimpleStringProperty(nickname);
        });
        colorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getColor().name()));
    }

    public void setGui(GUI gui) {
        this.gui = gui;
        refreshAvailableColors();
    }

    public void refreshFromModel() {
        if (gui == null) {
            return;
        }
        LobbyInfo lobby = gui.getLocalModel().getOwnLobby();
        if (lobby == null) {
            lobbyIdLabel.setText("-");
            lobbyStatusLabel.setText("-");
            waitingLabel.setText("Waiting for lobby data...");
            playersTable.setItems(FXCollections.observableArrayList());
            refreshAvailableColors();
            return;
        }
        lobbyIdLabel.setText(String.valueOf(lobby.getLobbyId()));
        lobbyStatusLabel.setText(lobby.getCurrentPlayers() + " / " + lobby.getNumPlayers());

        List<ClientPlayer> players = gui.getLocalModel().getAllPlayers();
        playersTable.setItems(FXCollections.observableArrayList(players));
        playersTable.refresh();

        boolean waiting = lobby.getCurrentPlayers() != lobby.getNumPlayers();
        waitingLabel.setText(waiting ? "Waiting for players to connect..." : "Lobby is full.");

        boolean needsJoinForm = gui.getLocalModel().getOwnPlayer() == null;
        joinFormTitle.setVisible(needsJoinForm);
        joinFormTitle.setManaged(needsJoinForm);
        joinFormGrid.setVisible(needsJoinForm);
        joinFormGrid.setManaged(needsJoinForm);
        nicknameField.setVisible(needsJoinForm);
        nicknameField.setManaged(needsJoinForm);
        colorCombo.setVisible(needsJoinForm);
        colorCombo.setManaged(needsJoinForm);
        joinGameButton.setVisible(needsJoinForm);
        joinGameButton.setManaged(needsJoinForm);
        refreshAvailableColors();
    }

    @FXML
    private void onJoinGameClicked() {
        if (gui == null) {
            showError("GUI not initialized.");
            return;
        }
        String nickname = nicknameField.getText() == null ? "" : nicknameField.getText().trim();
        if (!nickname.matches("^[a-zA-Z0-9]{3,12}$")) {
            showError("Nickname must be 3-12 alphanumeric chars.");
            return;
        }
        if (!gui.getLocalModel().isNicknameAvailable(nickname)) {
            showError("Nickname already in use.");
            return;
        }
        Color color = colorCombo.getValue();
        if (color == null) {
            showError("Please choose a color.");
            return;
        }
        if (!gui.getLocalModel().isColorAvailable(color)) {
            showError("Color already in use.");
            return;
        }
        gui.joinGame(nickname, color);
        clearError();
        showInfo("Joining game...");
    }

    public void showError(String message) {
        this.errorLabel.setText(message);
    }

    public void showInfo(String message) {
        this.infoLabel.setText(message);
    }

    private void clearError() {
        this.errorLabel.setText("");
    }

    private void refreshAvailableColors() {
        if (gui == null) {
            return;
        }
        Color currentSelection = colorCombo.getValue();
        colorCombo.setItems(FXCollections.observableArrayList(gui.getLocalModel().getAvailableColors()));
        if (currentSelection != null && gui.getLocalModel().isColorAvailable(currentSelection)) {
            colorCombo.setValue(currentSelection);
        } else {
            colorCombo.setValue(null);
        }
    }
}
