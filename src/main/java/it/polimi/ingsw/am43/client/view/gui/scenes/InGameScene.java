package it.polimi.ingsw.am43.client.view.gui.scenes;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.client.view.gui.components.CardNode;
import it.polimi.ingsw.am43.client.view.gui.components.OfferTrackCardNode;
import it.polimi.ingsw.am43.client.view.gui.components.OrderQueueNode;
import it.polimi.ingsw.am43.client.view.gui.components.PlayerTribeNode;
import it.polimi.ingsw.am43.client.view.gui.components.TotemNode;
import it.polimi.ingsw.am43.client.view.gui.GUI;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import javafx.fxml.FXML;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class InGameScene extends CustomScene {
    private static final int MAX_ACTIVITY_MESSAGES = 8;

    @FXML
    private Label eraLabel;
    @FXML
    private Label phaseLabel;
    @FXML
    private Label currentPlayerLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private FlowPane topRowPane;
    @FXML
    private FlowPane bottomRowPane;
    @FXML
    private HBox offerTrackPane;
    @FXML
    private StackPane orderQueuePane;
    @FXML
    private VBox scorePanel;
    @FXML
    private StackPane tribePane;
    @FXML
    private VBox activityLogPane;

    private List<Color> offerSelectionOrder = List.of();
    private final List<OfferAction> consumedOfferActions = new ArrayList<>();
    private GamePhase actionContextPhase;
    private String actionContextPlayer;
    private OfferAction pendingPickAction;
    private Integer pendingPickCardId;
    private String viewedTribeNickname;

    @Override
    public void setGui(GUI gui) {
        this.gui = gui;
        this.refreshFromModel();
    }

    @Override
    public void refreshFromModel() {
        if (this.gui == null) {
            return;
        }
        ClientModel model = this.gui.getLocalModel();
        this.eraLabel.setText("Era " + model.getCurrentEra());
        this.phaseLabel.setText(model.getPhase() == null ? "Phase: -" : "Phase: " + model.getPhase());
        this.currentPlayerLabel.setText("Current player: " + this.emptyFallback(model.getCurrentPlayerNickname()));

        this.updateActionContext(model);
        this.confirmPendingPick(model);
        List<OfferAction> availableActions = this.getCurrentAvailableActions(model);

        this.renderCardRow(this.topRowPane, model.getTopRowCards(), OfferAction.TOP, availableActions);
        this.renderCardRow(this.bottomRowPane, model.getBottomRowCards(), OfferAction.BOTTOM, availableActions);
        this.renderOfferTrack(model.getOfferTrack());
        this.renderOrderQueue(model);
        this.renderScoreboard(model.getAllPlayers(), model.getCurrentPlayerNickname());
        this.renderPlayerTribe(model);
    }

    @Override
    public void showInfo(String message) {
        this.setStatusStyle("status-info");
        this.statusLabel.setText(message);
        this.addActivityMessage(message, "activity-info");
    }

    @Override
    public void showError(String message) {
        if (this.rollbackPendingPickAction()) {
            this.refreshFromModel();
        }
        this.setStatusStyle("status-error");
        this.statusLabel.setText(message);
        this.addActivityMessage(message, "activity-error");
    }

    private void setStatusStyle(String styleClass) {
        this.statusLabel.getStyleClass().removeAll("status-info", "status-error");
        this.statusLabel.getStyleClass().add(styleClass);
    }

    private void addActivityMessage(String message, String styleClass) {
        if (this.activityLogPane == null || message == null || message.isBlank()) {
            return;
        }
        Label entry = new Label(message);
        entry.setMaxWidth(Double.MAX_VALUE);
        entry.setWrapText(true);
        entry.getStyleClass().addAll("activity-message", styleClass);
        this.activityLogPane.getChildren().add(0, entry);
        while (this.activityLogPane.getChildren().size() > MAX_ACTIVITY_MESSAGES) {
            this.activityLogPane.getChildren().remove(this.activityLogPane.getChildren().size() - 1);
        }

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(180), entry);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void renderCardRow(FlowPane pane, List<Integer> cardIds, OfferAction rowAction, List<OfferAction> availableActions) {
        pane.getChildren().clear();
        if (cardIds.isEmpty()) {
            pane.getChildren().add(new Label("No cards"));
            return;
        }
        boolean rowSelectable = availableActions.contains(rowAction);
        for (int cardId : cardIds) {
            CardNode cardNode = new CardNode(cardId);
            cardNode.setCardDisabled(!rowSelectable);
            cardNode.setClickable(rowSelectable);
            cardNode.setHighlighted(rowSelectable);
            if (rowSelectable) {
                cardNode.setOnCardSelected(() -> this.handleCardSelection(cardId, rowAction));
            }
            pane.getChildren().add(cardNode);
        }
    }

    private void renderOfferTrack(List<OfferTrackElement> offerTrack) {
        this.offerTrackPane.getChildren().clear();
        for (int i = 0; i < offerTrack.size(); i++) {
            OfferTrackElement slot = offerTrack.get(i);
            OfferTrackCardNode node = new OfferTrackCardNode(i, slot, this::isCurrentPlayerColor);
            this.configureOfferTrackDropTarget(node, i, slot);
            this.offerTrackPane.getChildren().add(node);
        }
    }

    private void renderOrderQueue(ClientModel model) {
        this.orderQueuePane.getChildren().clear();
        int numPlayers = model.getNumPlayers();
        List<Optional<Color>> orderQueueSlots = this.buildOrderQueueSlots(model);
        this.orderQueuePane.getChildren().add(new OrderQueueNode(numPlayers, orderQueueSlots, this::isCurrentPlayerColor, this::configureOrderQueueDragSource));
    }

    private List<Optional<Color>> buildOrderQueueSlots(ClientModel model) {
        List<Color> currentQueue = model.getOrderQueue();
        if (model.getPhase() != GamePhase.OFFER_TRACK_SELECTION) {
            this.offerSelectionOrder = List.of();
            return this.toOccupiedSlots(currentQueue);
        }
        if (currentQueue.size() == model.getNumPlayers() || this.offerSelectionOrder.isEmpty()) {
            this.offerSelectionOrder = new ArrayList<>(currentQueue);
        }

        Set<Color> remainingColors = new HashSet<>(currentQueue);
        return this.offerSelectionOrder.stream()
                .map(color -> remainingColors.contains(color) ? Optional.of(color) : Optional.<Color>empty())
                .toList();
    }

    private List<Optional<Color>> toOccupiedSlots(List<Color> orderQueue) {
        return orderQueue.stream()
                .map(Optional::of)
                .toList();
    }

    private void renderScoreboard(List<ClientPlayer> players, String currentPlayer) {
        this.scorePanel.getChildren().clear();
        for (ClientPlayer player : players) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("scoreboard-row");
            row.setCursor(Cursor.HAND);
            boolean currentPlayerRow = player.getNickname().equals(currentPlayer);
            boolean viewedPlayerRow = player.getNickname().equals(this.viewedTribeNickname);
            if (currentPlayerRow) {
                row.getStyleClass().add("current-player-row");
            }
            if (viewedPlayerRow) {
                row.getStyleClass().add("viewed-player-row");
            }
            row.setOnMouseClicked(event -> {
                this.viewedTribeNickname = player.getNickname();
                this.renderScoreboard(players, currentPlayer);
                this.renderPlayerTribe(this.gui.getLocalModel());
                event.consume();
            });
            TotemNode totem = new TotemNode(player.getColor(), 24.0);
            totem.setHighlighted(currentPlayerRow || viewedPlayerRow);
            Label text = new Label(String.format("%s%s  Food: %d  Prestige: %d",
                    currentPlayerRow ? "> " : "  ",
                    player.getNickname(),
                    player.getFood(),
                    player.getPrestigePoints()));
            text.setStyle("-fx-text-fill: " + this.cssColor(player.getColor()) + ";");
            row.getChildren().addAll(totem, text);
            this.scorePanel.getChildren().add(row);
        }
    }

    private void renderPlayerTribe(ClientModel model) {
        this.tribePane.getChildren().clear();
        ClientPlayer viewedPlayer = this.getViewedTribePlayer(model);
        if (viewedPlayer == null) {
            return;
        }
        boolean ownTribe = model.getOwnPlayer() != null && viewedPlayer.getNickname().equals(model.getOwnPlayer().getNickname());
        this.tribePane.getChildren().add(new PlayerTribeNode(viewedPlayer, ownTribe));
    }

    private ClientPlayer getViewedTribePlayer(ClientModel model) {
        ClientPlayer ownPlayer = model.getOwnPlayer();
        if (ownPlayer == null) {
            return null;
        }
        if (this.viewedTribeNickname == null || model.getPlayerByNickname(this.viewedTribeNickname) == null) {
            this.viewedTribeNickname = ownPlayer.getNickname();
        }
        return model.getPlayerByNickname(this.viewedTribeNickname);
    }

    private String cssColor(Color color) {
        if (color == null) {
            return "#5f5a52";
        }
        return TotemNode.toPantoneHex(color);
    }

    private boolean isCurrentPlayerColor(Color color) {
        ClientModel model = this.gui.getLocalModel();
        ClientPlayer currentPlayer = model.getPlayerByNickname(model.getCurrentPlayerNickname());
        return currentPlayer != null && currentPlayer.getColor().equals(color);
    }

    private void configureOrderQueueDragSource(TotemNode totem, Color color) {
        boolean draggable = this.canDragTotem(color);
        totem.setDraggable(draggable);
        if (!draggable) {
            return;
        }
        totem.setOnDragDetected(event -> {
            Dragboard dragboard = totem.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(color.name());
            dragboard.setContent(content);
            dragboard.setDragView(totem.snapshot(null, null));
            event.consume();
        });
    }

    private void configureOfferTrackDropTarget(StackPane target, int index, OfferTrackElement slot) {
        target.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        target.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = dragboard.hasString() && this.canDropTotem(dragboard.getString(), slot, true);
            if (success) {
                this.gui.getLocalModel().startValidation();
                this.refreshFromModel();
                this.gui.submitTask(() -> this.controller.placeTotem(index));
                this.showInfo("Placing totem...");
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void handleCardSelection(int cardId, OfferAction rowAction) {
        if (!this.canPickCard(rowAction, true)) {
            return;
        }
        this.gui.getLocalModel().startValidation();
        this.consumedOfferActions.add(rowAction);
        this.pendingPickAction = rowAction;
        this.pendingPickCardId = cardId;
        this.refreshFromModel();
        this.gui.submitTask(() -> this.controller.pickCard(cardId));
        this.showInfo("Picking card...");
    }

    private boolean canPickCard(OfferAction rowAction, boolean showFeedback) {
        ClientModel model = this.gui.getLocalModel();
        if (model.isValidating()) {
            if (showFeedback) this.showError("Last command is still being processed.");
            return false;
        }
        if (!model.isOwnTurn()) {
            if (showFeedback) this.showError("Please wait for your turn.");
            return false;
        }
        if (!this.isCardPickPhase(model.getPhase())) {
            if (showFeedback) this.showError("You are not allowed to perform this action in this phase.");
            return false;
        }
        if (!this.getCurrentAvailableActions(model).contains(rowAction)) {
            if (showFeedback) this.showError("You cannot pick a card from this row now.");
            return false;
        }
        return true;
    }

    private List<OfferAction> getCurrentAvailableActions(ClientModel model) {
        if (!model.isOwnTurn() || model.isValidating() || !this.isCardPickPhase(model.getPhase())) {
            return List.of();
        }
        List<OfferAction> actions = this.getCurrentOfferActions(model);
        return this.removeConsumedActions(actions);
    }

    private List<OfferAction> getCurrentOfferActions(ClientModel model) {
        if (model.getPhase() == GamePhase.DRAW_FROM_TOP_BONUS_ACTION) {
            return List.of(OfferAction.TOP);
        }

        ClientPlayer currentPlayer = model.getPlayerByNickname(model.getCurrentPlayerNickname());
        if (currentPlayer == null) {
            return List.of();
        }
        for (OfferTrackElement slot : model.getOfferTrack()) {
            if (currentPlayer.getColor().equals(slot.getColor())) {
                return slot.getOfferActions();
            }
        }
        return List.of();
    }

    private List<OfferAction> removeConsumedActions(List<OfferAction> actions) {
        List<OfferAction> remainingActions = new ArrayList<>(actions);
        for (OfferAction consumedAction : this.consumedOfferActions) {
            remainingActions.remove(consumedAction);
        }
        return remainingActions;
    }

    private void updateActionContext(ClientModel model) {
        if (!this.isCardPickPhase(model.getPhase())) {
            this.clearActionContext();
            return;
        }
        if (!Objects.equals(this.actionContextPlayer, model.getCurrentPlayerNickname())
                || this.actionContextPhase != model.getPhase()) {
            this.consumedOfferActions.clear();
            this.pendingPickAction = null;
            this.pendingPickCardId = null;
            this.actionContextPlayer = model.getCurrentPlayerNickname();
            this.actionContextPhase = model.getPhase();
        }
    }

    private void confirmPendingPick(ClientModel model) {
        if (this.pendingPickCardId == null) {
            return;
        }
        boolean cardStillOnBoard = model.getTopRowCards().contains(this.pendingPickCardId)
                || model.getBottomRowCards().contains(this.pendingPickCardId);
        if (!cardStillOnBoard) {
            this.pendingPickAction = null;
            this.pendingPickCardId = null;
        }
    }

    private boolean rollbackPendingPickAction() {
        if (this.pendingPickAction != null) {
            this.consumedOfferActions.remove(this.pendingPickAction);
            this.pendingPickAction = null;
            this.pendingPickCardId = null;
            return true;
        }
        return false;
    }

    private void clearActionContext() {
        this.consumedOfferActions.clear();
        this.pendingPickAction = null;
        this.pendingPickCardId = null;
        this.actionContextPlayer = null;
        this.actionContextPhase = null;
    }

    private boolean isCardPickPhase(GamePhase phase) {
        return phase == GamePhase.ACTION_RESOLUTION || phase == GamePhase.DRAW_FROM_TOP_BONUS_ACTION;
    }

    private boolean canDragTotem(Color color) {
        ClientModel model = this.gui.getLocalModel();
        ClientPlayer ownPlayer = model.getOwnPlayer();
        return ownPlayer != null
                && ownPlayer.getColor().equals(color)
                && model.isOwnTurn()
                && model.getPhase() == GamePhase.OFFER_TRACK_SELECTION
                && !model.isValidating();
    }

    private boolean canDropTotem(String colorName, OfferTrackElement slot, boolean showFeedback) {
        ClientModel model = this.gui.getLocalModel();
        ClientPlayer ownPlayer = model.getOwnPlayer();
        if (ownPlayer == null || !ownPlayer.getColor().name().equals(colorName)) {
            if (showFeedback) this.showError("You can only place your own totem.");
            return false;
        }
        if (model.isValidating()) {
            if (showFeedback) this.showError("Last command is still being processed.");
            return false;
        }
        if (!model.isOwnTurn()) {
            if (showFeedback) this.showError("Please wait for your turn.");
            return false;
        }
        if (model.getPhase() != GamePhase.OFFER_TRACK_SELECTION) {
            if (showFeedback) this.showError("You cannot place a totem in this phase.");
            return false;
        }
        if (slot.getColor() != null) {
            if (showFeedback) this.showError("This offer card is already occupied.");
            return false;
        }
        return true;
    }

    private String emptyFallback(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

}
