package it.polimi.ingsw.am43.client.view.gui.scenes;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.client.view.gui.components.CardNode;
import it.polimi.ingsw.am43.client.view.gui.components.OfferTrackCardNode;
import it.polimi.ingsw.am43.client.view.gui.components.OrderQueueNode;
import it.polimi.ingsw.am43.client.view.gui.components.OrderQueueSlot;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InGameScene extends CustomScene {
    private static final int MAX_ACTIVITY_MESSAGES = 8;
    private static final String FOOD_ICON_PATH = "/it/polimi/ingsw/am43/images/food&prestige/food.png";
    private static final String PRESTIGE_ICON_PATH = "/it/polimi/ingsw/am43/images/food&prestige/prestige_point.png";
    private static final double SCORE_ICON_SIZE = 30.0;

    @FXML
    private Label eraLabel;
    @FXML
    private Label phaseLabel;
    @FXML
    private Label currentPlayerLabel;
    @FXML
    private FlowPane topRowPane;
    @FXML
    private FlowPane bottomRowPane;
    @FXML
    private HBox offerTrackPane;
    @FXML
    private StackPane orderQueuePane;
    @FXML
    private Button endTurnButton;
    @FXML
    private VBox scorePanel;
    @FXML
    private StackPane tribePane;
    @FXML
    private VBox activityLogPane;
    @FXML
    private AnchorPane endGameLayer;
    @FXML
    private Label winnerLabel;
    @FXML
    private Label numPlayersLabel;
    @FXML
    private Label rankLabel;
    @FXML
    private Label leaderboardTitleLabel;
    @FXML
    private ListView<LeaderboardEntry> leaderboardList;

    private final List<OfferAction> consumedOfferActions = new ArrayList<>();
    private GamePhase actionContextPhase;
    private String actionContextPlayer;
    private OfferAction pendingPickAction;
    private Integer pendingPickCardId;
    private String viewedTribeNickname;

    @FXML
    private void initialize() {
        this.leaderboardList.setPlaceholder(new Label("No leaderboard entries yet."));
        this.leaderboardList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(LeaderboardEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty || entry == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(createLeaderboardRow(entry));
                }
            }
        });
    }

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
        this.updateEndTurnButton(model);
        this.renderScoreboard(model.getAllPlayers(), model.getCurrentPlayerNickname());
        this.renderPlayerTribe(model);
    }

    @Override
    public void showInfo(String message) {
        this.addActivityMessage(message, "activity-info");
    }

    @Override
    public void showError(String message) {
        if (this.rollbackPendingPickAction()) {
            this.refreshFromModel();
        }
        this.addActivityMessage(message, "activity-error");
    }

    @Override
    public void showDisconnectedPlayer(String nickname) {
        this.refreshFromModel();
        this.showError("Player " + nickname + " lost connection. Waiting for reconnection.");
    }

    @Override
    public void showPlayerReconnection(String nickname) {
        this.refreshFromModel();
        this.showInfo("Player " + nickname + " reconnected.");
    }

    private void addActivityMessage(String message, String styleClass) {
        if (this.activityLogPane == null || message == null || message.isBlank()) {
            return;
        }
        Label entry = new Label(message);
        entry.setMaxWidth(Double.MAX_VALUE);
        entry.setMinHeight(Region.USE_PREF_SIZE);
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
        List<OrderQueueSlot> orderQueueSlots = this.buildOrderQueueSlots(model);
        this.orderQueuePane.getChildren().add(new OrderQueueNode(numPlayers, orderQueueSlots, this::isCurrentPlayerColor, this::configureOrderQueueDragSource));
    }

    private void updateEndTurnButton(ClientModel model) {
        this.endTurnButton.setDisable(!this.canEndTurn(model, false));
    }

    private List<OrderQueueSlot> buildOrderQueueSlots(ClientModel model) {
        List<Color> currentQueue = model.getOrderQueue();
        List<OrderQueueSlot> slots = new ArrayList<>();
        int emptySlots = Math.max(0, model.getNumPlayers() - currentQueue.size());
        for (int i = 0; i < emptySlots; i++) {
            slots.add(OrderQueueSlot.empty());
        }
        currentQueue.stream()
                .map(OrderQueueSlot::active)
                .forEach(slots::add);
        return slots;
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
            boolean disconnectedPlayerRow = player.isDisconnected();
            if (currentPlayerRow) {
                row.getStyleClass().add("current-player-row");
            }
            if (viewedPlayerRow) {
                row.getStyleClass().add("viewed-player-row");
            }
            if (disconnectedPlayerRow) {
                row.getStyleClass().add("disconnected-player-row");
            }
            row.setOnMouseClicked(event -> {
                this.viewedTribeNickname = player.getNickname();
                this.renderScoreboard(players, currentPlayer);
                this.renderPlayerTribe(this.gui.getLocalModel());
                event.consume();
            });
            TotemNode totem = new TotemNode(player.getColor(), 24.0);
            totem.setHighlighted(currentPlayerRow || viewedPlayerRow);
            Label name = new Label((currentPlayerRow ? "> " : "") + this.scoreboardName(player));
            name.setMaxWidth(92.0);
            name.setTextOverrun(OverrunStyle.ELLIPSIS);
            name.getStyleClass().add("scoreboard-name");
            name.setStyle("-fx-text-fill: " + this.cssColor(player.getColor()) + ";");

            row.getChildren().addAll(
                    totem,
                    name,
                    this.createScoreIcon(FOOD_ICON_PATH, "F"),
                    this.createScoreValue(player.getFood()),
                    this.createScoreIcon(PRESTIGE_ICON_PATH, "P"),
                    this.createScoreValue(player.getPrestigePoints())
            );
            this.scorePanel.getChildren().add(row);
        }
    }

    private Label createScoreValue(int value) {
        Label score = new Label(String.valueOf(value));
        score.getStyleClass().add("scoreboard-value");
        return score;
    }

    private Region createScoreIcon(String iconPath, String fallbackText) {
        Image image = this.loadImage(iconPath);
        if (image == null) {
            Label fallback = new Label(fallbackText);
            fallback.getStyleClass().add("scoreboard-icon-fallback");
            return fallback;
        }
        ImageView icon = new ImageView(image);
        icon.setFitWidth(SCORE_ICON_SIZE);
        icon.setFitHeight(SCORE_ICON_SIZE);
        icon.setPreserveRatio(true);
        StackPane iconContainer = new StackPane(icon);
        iconContainer.getStyleClass().add("scoreboard-icon");
        return iconContainer;
    }

    private Image loadImage(String path) {
        try {
            return new Image(Objects.requireNonNull(this.getClass().getResource(path)).toExternalForm());
        } catch (NullPointerException exception) {
            return null;
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

    private String scoreboardName(ClientPlayer player) {
        if (player.isDisconnected()) {
            return player.getNickname() + "  reconnecting...";
        }
        return player.getNickname();
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

    @FXML
    private void onEndTurnClicked() {
        ClientModel model = this.gui.getLocalModel();
        if (!this.canEndTurn(model, true)) {
            return;
        }
        model.startValidation();
        this.refreshFromModel();
        this.gui.submitTask(this.controller::endTurn);
        this.showInfo("Ending turn...");
    }

    private boolean canEndTurn(ClientModel model, boolean showFeedback) {
        if (model.isValidating()) {
            if (showFeedback) this.showError("Last command is still being processed.");
            return false;
        }
        if (!model.isOwnTurn()) {
            if (showFeedback) this.showError("Please wait for your turn.");
            return false;
        }
        if (model.getPhase() != GamePhase.ACTION_RESOLUTION) {
            if (showFeedback) this.showError("You are not allowed to perform this action in this phase.");
            return false;
        }
        return true;
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

    private HBox createLeaderboardRow(LeaderboardEntry entry) {
        Label position = new Label("#" + entry.position());
        Label nickname = new Label(entry.nickname());
        Label score = new Label(entry.score() + " punti");
        Label date = new Label(entry.date() == null ? "-" : entry.date().toString());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(20, position, nickname, spacer, score, date);
        row.getStyleClass().add("lobby-info");

        return row;
    }

    public record LeaderboardEntry(int position, String nickname, int score, Object date) {
    }

}
