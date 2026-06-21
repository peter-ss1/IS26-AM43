package it.polimi.ingsw.am43.client.view.gui.scenes;

import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.client.view.gui.components.LobbyElement;
import it.polimi.ingsw.am43.model.enums.Color;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Scene controller for the lobby selection and creation screen.
 * Allows users to choose an existing lobby from a list or to create a new one.
 */
public class LobbyChoiceScene extends CustomScene {
    @FXML
    private ListView<LobbyInfo> lobbyInfoList;
    @FXML
    private TextField nicknameField;
    @FXML
    private ToggleGroup colorGroup;
    @FXML
    private ToggleGroup numberGroup;
    @FXML
    private Button joinLobbyButton;
    @FXML
    private Button createLobbyButton;

    private final ObservableList<LobbyInfo> lobbies = FXCollections.observableArrayList();
    private final BooleanProperty validating = new SimpleBooleanProperty(false);

    /**
     * JavaFX initialization method. Establishes the custom cell rendering factory
     * and sets a placeholder in case of empty list.
     */
    @FXML
    private void initialize() {
        this.lobbyInfoList.setPlaceholder(new Label("No active lobbies found. Create one!"));
        this.lobbyInfoList.setCellFactory(_ -> new LobbyElement());
    }

    /**
     * Populates active lobbies from the local cache and configures UI state bindings.
     */
    @Override
    public void setUp() {
        this.lobbies.setAll(this.gui.getLocalModel().getLobbies());
        this.lobbyInfoList.setItems(this.lobbies);

        this.joinLobbyButton.disableProperty().bind(
                this.lobbyInfoList.getSelectionModel().selectedItemProperty().isNull().or(this.validating)
        );
        this.createLobbyButton.disableProperty().bind(
                this.nicknameField.textProperty().isEmpty().or(this.colorGroup.selectedToggleProperty().isNull()).or(this.numberGroup.selectedToggleProperty().isNull())
        );
        this.colorGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
            }
        });
        this.numberGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
            }
        });
    }

    /**
     * Clears user text fields and resets button labels.
     */
    @Override
    public void reset() {
        this.nicknameField.clear();
        this.joinLobbyButton.setText("JOIN LOBBY");
        this.createLobbyButton.setText("CREATE LOBBY");
        this.validating.set(false);
    }

    /**
     * Extracts selected index pointers and dispatches an asynchronous lobby join request.
     */
    @FXML
    private void onJoinClicked() {
        this.validating.set(true);
        this.joinLobbyButton.setText("JOINING LOBBY");
        this.gui.submitTask(() -> this.controller.joinLobby(this.lobbyInfoList.getSelectionModel().getSelectedItem().getLobbyId()));
    }

    /**
     * Validates user input fields and dispatches an asynchronous lobby creation request.
     */
    @FXML
    private void onCreateClicked() {
        this.validating.set(true);
        String nickname = nicknameField.getText() == null ? "" : nicknameField.getText().trim();
        if (!nickname.matches("^[a-zA-Z0-9]{3,12}$")) {
            this.nicknameField.clear();
            this.nicknameField.setPromptText("Enter a valid nickname");
            return;
        }
        int index = this.colorGroup.getToggles().indexOf(this.colorGroup.getSelectedToggle());
        Color color = Color.values()[index];
        int num = this.numberGroup.getToggles().indexOf(this.numberGroup.getSelectedToggle()) + 2;
        this.createLobbyButton.setText("CREATING LOBBY");
        this.gui.submitTask(() -> this.controller.createLobby(nickname, color, num));
    }

    /**
     * UI update syncing the lobby cache with the local model data.
     */
    @Override
    public void showAvailableLobbies() {
        this.lobbies.setAll(this.gui.getLocalModel().getLobbies());
    }
}
