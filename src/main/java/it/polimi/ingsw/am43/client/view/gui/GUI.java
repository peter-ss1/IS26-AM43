package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.PointsPair;
import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.client.view.ViewState;
import it.polimi.ingsw.am43.client.view.gui.components.CardMetadataRegistry;
import it.polimi.ingsw.am43.client.view.gui.components.PopUp;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.database.RankElement;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

/**
 * Main Graphical User Interface orchestrator. Implements {@link UI} to offer
 * a single standard access point for view updates.
 */
public class GUI implements UI {
    private final GuiApplication app;
    private final ClientModel localModel;
    private final ClientController controller;
    private final GuiNavigator navigator;
    private final PopUp popUp;
    private ViewState state;

    /**
     * Initializes the GUI architecture, spawning the local model, client-side controller, background task runner,
     * navigation engine, and PopUp element.
     *
     * @param application Main JavaFX application wrapper.
     * @param stage       Primary window display canvas.
     */
    public GUI(GuiApplication application, Stage stage) {
        this.app = application;
        this.localModel = new ClientModel(this);
        this.controller = new ClientController(this, this.localModel);
        this.navigator = new GuiNavigator(stage, this, this.controller);
        this.navigator.init();
        this.popUp = new PopUp(stage);
        this.state = ViewState.CONNECTION;
    }

    /**
     * @return client-side {@link ClientModel} with simplified state.
     */
    public ClientModel getLocalModel() {
        return this.localModel;
    }

    /**
     * Submits a task to execute asynchronously on a background worker thread.
     *
     * @param task Action to run.
     */
    public void submitTask(Runnable task) {
        this.app.runAsync(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showAvailableLobbies() {
        if (this.state != ViewState.LOBBY_CHOICE) return;
        Platform.runLater(() -> this.navigator.getCurrentScene().showAvailableLobbies());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterLobby() {
        this.state = this.localModel.getOwnPlayer() == null ? ViewState.IN_LOBBY_CHOICE : ViewState.IN_LOBBY;
        Platform.runLater(() -> {
            this.navigator.showScene(this.state);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showNewPlayer() {
        if (this.state != ViewState.IN_LOBBY && this.state != ViewState.IN_LOBBY_CHOICE) return;
        Platform.runLater(() -> navigator.getCurrentScene().showNewPlayer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showGameStart() {
        this.state = ViewState.IN_GAME;
        Platform.runLater(() -> {
            this.navigator.showScene(this.state);
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo("Game started.");
        });
    }

    /**
     * {@inheritDoc}
     * @param message  The descriptive error explanation.
     * @param creation {@code true} if the error occurred during lobby creation,
     * {@code false} if it occurred while trying to join.
     */
    @Override
    public void handleLobbyChoiceError(String message, boolean creation) {
        this.state = ViewState.LOBBY_CHOICE;
        Platform.runLater(() -> {
            this.navigator.showScene(this.state);
            String operation = creation ? "creation" : "join";
            this.showPopUp("Lobby " + operation + " failed: " + message);
            this.navigator.getCurrentScene().reset();
        });
    }

    /**
     * {@inheritDoc}
     * @param message The descriptive error explanation.
     */
    @Override
    public void handleLobbyJoinError(String message) {
        Platform.runLater(() -> {
            this.showPopUp("Lobby join failed: " + message);
            this.navigator.getCurrentScene().reset();
        });
    }

    /**
     * {@inheritDoc}
     * @param error The descriptive error explanation.
     */
    @Override
    public void showGameError(String error) {
        this.localModel.stopValidation();
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showError(error);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showNewCurrPlayer() {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            if (this.localModel.isOwnTurn()) {
                this.navigator.getCurrentScene().showInfo("It's now your turn.");
            } else {
                this.navigator.getCurrentScene().showInfo("Current player is now " + this.localModel.getCurrentPlayerNickname() + ".");
            }
        });
    }

    /**
     * {@inheritDoc}
     * @param nickname The player who performed the action.
     * @param position The zero-indexed position of totem placement.
     */
    @Override
    public void showTotemPlaced(String nickname, int position) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " placed a totem in position " + (position + 1) + ".");
        });
    }

    /**
     * {@inheritDoc}
     * @param nickname The player who performed the action.
     * @param cardId    The numeric identifier of the picked card.
     * @param finalPick {@code true} if this action concludes the player's turn.
     */
    @Override
    public void showCardPicked(String nickname, int cardId, boolean finalPick) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " picked " + this.cardName(cardId) + ".");
        });
    }

    /**
     * {@inheritDoc}
     * @param nickname The affected player.
     * @param cost     The amount of food consumed.
     */
    @Override
    public void showBuildingAcquisition(String nickname, int cost) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " paid " + cost + " food to buy a building.");
        });
    }

    /**
     * {@inheritDoc}
     * @param nickname The affected player.
     * @param food     The amount of food procured.
     */
    @Override
    public void showHunterEffect(String nickname, int food) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " received " + food + " food from a hunter.");
        });
    }

    /**
     * {@inheritDoc}
     * @param nickname The affected player.
     * @param bonus    The numerical amount of resource involved.
     * @param resource The type of resource involved.
     */
    @Override
    public void showBuildingEffect(String nickname, int bonus, String resource) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " received " + bonus + " " + resource + " from a building.");
        });
    }

    /**
     * {@inheritDoc}
     * @param effects A map pairing player nicknames with their respective adjustments
     * in food and prestige points.
     */
    @Override
    public void showHuntEvent(Map<String, PointsPair> effects) {
        refreshGameSceneWithInfo("Hunt event resolved. " + this.formatFoodPrestigeEffects(effects));
    }

    /**
     * {@inheritDoc}
     * @param effects A map pairing player nicknames with their respective adjustments
     * in food.
     */
    @Override
    public void showPaintingEvent(Map<String, Integer> effects) {
        refreshGameSceneWithInfo("Painting event resolved. " + this.formatSingleResourceEffects(effects, "food"));
    }

    /**
     * {@inheritDoc}
     * @param effects A map pairing player nicknames with their respective adjustments
     * in food and prestige points.
     */
    @Override
    public void showSustenanceEvent(Map<String, PointsPair> effects) {
        refreshGameSceneWithInfo("Sustenance event resolved. " + this.formatFoodPrestigeEffects(effects));
    }

    /**
     * {@inheritDoc}
     * @param effects A map pairing player nicknames with their respective adjustments
     * in prestige points.
     */
    @Override
    public void showRitualEvent(Map<String, Integer> effects) {
        refreshGameSceneWithInfo("Ritual event resolved. " + this.formatSingleResourceEffects(effects, "prestige points"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showFinalPoints() {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
        });
    }

    /**
     * {@inheritDoc}
     * @param nickname The affected player.
     * @param modifier The involved bonus/malus.
     * @param prestige {@code true} if the change alters prestige points,
     * {@code false} if it applies to food.
     */
    @Override
    public void showOrderModifier(String nickname, int modifier, boolean prestige) {
        if (this.state != ViewState.IN_GAME) return;
        String resource = prestige ? "prestige points" : "food";
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " changed " + resource + " by " + modifier + ".");
        });
    }

    /**
     * {@inheritDoc}
     * @param nickname The affected player.
     */
    @Override
    public void showFoodOffer(String nickname) {
        refreshGameSceneWithInfo(nickname + " received 3 food from the offer card.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showRetrievedInfo() {
        Platform.runLater(this.navigator::showDisconnectionMenu);
    }

    /**
     * {@inheritDoc}
     * @param nickname The disconnected player.
     */
    @Override
    public void showDisconnectedPlayer(String nickname) {
        Platform.runLater(() -> this.navigator.getCurrentScene().showDisconnectedPlayer(nickname));
    }

    /**
     * {@inheritDoc}
     * @param nickname The reconnected player.
     */
    @Override
    public void showPlayerReconnection(String nickname) {
        Platform.runLater(() -> this.navigator.getCurrentScene().showPlayerReconnection(nickname));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showDisconnection() {
        Platform.runLater(this.navigator::showLoading);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterLobbyChoice() {
        this.state = ViewState.LOBBY_CHOICE;
        Platform.runLater(() -> this.navigator.showScene(this.state));
    }

    /**
     * {@inheritDoc}
     * @param leaderboard A sorted list with global historical ranking info.
     * @param myRank      The client position within global historical ranking.
     */
    @Override
    public void showGameEnd(List<RankElement> leaderboard, int myRank) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().showGameEnd(leaderboard, myRank);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showRoundEnding() {
    }

    /**
     * {@inheritDoc}
     * @param length The duration of the timer in seconds.
     */
    @Override
    public void showTimer(int length) {
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showError("You are the only player left. Timer started of length: " + length);
        });
    }

    /**
     * Helper method to refresh the current scene and display a given message.
     *
     * @param message The message to display.
     */
    private void refreshGameSceneWithInfo(String message) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(message);
        });
    }

    /**
     * Helper method to format an event resolution data map.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     *                in food and prestige points.
     * @return the formatted string.
     */
    private String formatFoodPrestigeEffects(Map<String, PointsPair> effects) {
        if (effects == null || effects.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        effects.forEach((nickname, values) -> {
            int food = values.getFood();
            int prestige = values.getPrestige();
            builder.append(nickname)
                    .append(": ")
                    .append(this.formatSigned(food))
                    .append(" food, ")
                    .append(this.formatSigned(prestige))
                    .append(" prestige.\n");
        });
        return builder.toString().trim();
    }

    /**
     * Helper method to format an event resolution data list.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     *                in specified resource.
     * @return the formatted string.
     */
    private String formatSingleResourceEffects(Map<String, Integer> effects, String resource) {
        if (effects == null || effects.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        effects.forEach((nickname, value) -> builder.append(nickname)
                .append(": ")
                .append(this.formatSigned(value))
                .append(" ")
                .append(resource)
                .append(".\n"));
        return builder.toString().trim();
    }

    private String formatSigned(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private String cardName(int cardId) {
        return CardMetadataRegistry.get(cardId).displayName();
    }

    /**
     * Displays a temporary informative pop-up.
     */
    public void showPopUp(String message) {
        this.popUp.show(message);
    }

    /**
     * Displays the game menu overlay panel.
     */
    public void showMenu() {
        Platform.runLater(this.navigator::showMenu);
    }

}
