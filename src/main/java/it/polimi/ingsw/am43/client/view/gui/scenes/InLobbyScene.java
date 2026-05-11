package it.polimi.ingsw.am43.client.view.gui.scenes;

import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.view.gui.components.LobbyPlayerNode;
import it.polimi.ingsw.am43.model.enums.Color;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;

public class InLobbyScene extends CustomScene {
    @FXML
    private Label lobbyId;
    @FXML
    private HBox banner;
    @FXML
    private TextField nicknameField;
    @FXML
    private ToggleGroup colorGroup;
    @FXML
    private HBox playerContainer;
    @FXML
    private Button confirmButton;

    private final BooleanProperty validating = new SimpleBooleanProperty(false);

    @FXML
    private void initialize() {
    }

    @Override
    public void setUp() {
        this.lobbyId.setText("Lobby ID: " + this.gui.getLocalModel().getOwnLobby().getLobbyId());
        if (this.gui.getLocalModel().getOwnPlayer() != null) {
            this.banner.getChildren().clear();
            Label text = new Label("  WAITING FOR GAME TO START...  ");
            text.getStyleClass().add("loginLabel");
            this.banner.getChildren().add(text);
        } else {
            this.confirmButton.disableProperty().bind(
                    this.nicknameField.textProperty().isEmpty().or(this.colorGroup.selectedToggleProperty().isNull())
            );
            this.colorGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) {
                    oldVal.setSelected(true);
                }
            });
            this.updateAvailableColors();
        }
        for (int i = 0; i < this.gui.getLocalModel().getOwnLobby().getNumPlayers(); i++) {
            this.playerContainer.getChildren().add(new LobbyPlayerNode());
        }
        this.updateLobbyPlayers();

    }

    private void updateLobbyPlayers() {
        List<ClientPlayer> allPlayers = this.gui.getLocalModel().getAllPlayers();
        String ownName = this.gui.getLocalModel().getOwnPlayer() == null ? "" : this.gui.getLocalModel().getOwnPlayer().getNickname();
        for (ClientPlayer p : allPlayers) {
            LobbyPlayerNode existingNode = findNodeForPlayer(p.getNickname());
            if (existingNode == null) {
                LobbyPlayerNode emptySlot = findFirstEmptySlot();
                if (emptySlot != null) {
                    emptySlot.activate(p.getNickname(), p.getColor(), p.isDisconnected(), p.getNickname().equals(ownName));
                }
            } else {
                existingNode.update(p.isDisconnected());
            }
        }
    }

    private LobbyPlayerNode findNodeForPlayer(String name) {
        return playerContainer.getChildren().stream().map(n -> (LobbyPlayerNode) n)
                .filter(lp -> !lp.isEmpty() && name.equals(lp.getNickname())).findFirst().orElse(null);
    }

    private LobbyPlayerNode findFirstEmptySlot() {
        return playerContainer.getChildren().stream().map(n -> (LobbyPlayerNode) n)
                .filter(LobbyPlayerNode::isEmpty).findFirst().orElse(null);
    }

    @Override
    public void reset() {
        this.nicknameField.clear();
        this.validating.set(false);
        this.confirmButton.setText("CONFIRM");
    }

    @FXML
    private void onConfirmClicked() {
        String nickname = nicknameField.getText() == null ? "" : nicknameField.getText().trim();
        if (!nickname.matches("^[a-zA-Z0-9]{3,12}$")) {
            this.nicknameField.clear();
            this.nicknameField.setPromptText("Invalid nickname");
            return;
        } else if (!this.gui.getLocalModel().isNicknameAvailable(nickname)) {
            this.nicknameField.clear();
            this.nicknameField.setPromptText("Nickname taken");
            return;
        }
        int index = this.colorGroup.getToggles().indexOf(this.colorGroup.getSelectedToggle());
        Color color = Color.values()[index];
        this.confirmButton.setText("CONFIRMING");
        this.gui.submitTask(() -> this.controller.joinGame(nickname, color));
    }

    @Override
    public void showNewPlayer() {
        if (this.gui.getLocalModel().getOwnPlayer() == null) this.updateAvailableColors();
        else {
            this.banner.getChildren().clear();
            Label text = new Label("  WAITING FOR GAME TO START...  ");
            text.getStyleClass().add("loginLabel");
            this.banner.getChildren().add(text);
        }
        updateLobbyPlayers();
    }

    @Override
    public void showPlayerReconnection(String nickname) {
        this.updateLobbyPlayers();
    }

    @Override
    public void showDisconnectedPlayer(String nickname) {
        this.updateLobbyPlayers();
    }

    public void updateAvailableColors() {
        List<Color> availableColors = this.gui.getLocalModel().getAvailableColors();
        List<Toggle> toggles = this.colorGroup.getToggles();

        for (int i = 0; i < toggles.size(); i++) {
            Color currentColor = Color.values()[i];
            Node node = (Node) toggles.get(i);
            if (availableColors.contains(currentColor)) {
                node.setDisable(false);
                node.setOpacity(1.0);
            } else {
                node.setDisable(true);
                node.setOpacity(0.3);
            }
            if (node.isDisable() && toggles.get(i).isSelected()) {
                this.colorGroup.selectToggle(null);
            }
        }
    }
}
