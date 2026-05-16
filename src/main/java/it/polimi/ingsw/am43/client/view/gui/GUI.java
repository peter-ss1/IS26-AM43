package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.PointsPair;
import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.client.view.ViewState;
import it.polimi.ingsw.am43.client.view.gui.components.CardMetadataRegistry;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.database.RankElement;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class GUI implements UI {
    private final GuiApplication app;
    private final ClientModel localModel;
    private final ClientController controller;
    private final GuiNavigator navigator;
    private final PopUp popUp;
    private ViewState state;

    public GUI(GuiApplication application, Stage stage) {
        this.app = application;
        this.localModel = new ClientModel(this);
        this.controller = new ClientController(this, this.localModel);
        this.navigator = new GuiNavigator(stage, this, this.controller);
        this.navigator.init();
        this.popUp = new PopUp(stage);
        this.state = ViewState.CONNECTION;
    }

    public ClientModel getLocalModel() {
        return localModel;
    }

    public void submitTask(Runnable task) {
        this.app.runAsync(task);
    }

    @Override
    public void showAvailableLobbies() {
        if (this.state != ViewState.LOBBY_CHOICE) return;
        Platform.runLater(() -> this.navigator.getCurrentScene().showAvailableLobbies());
    }

    @Override
    public void enterLobby() {
        this.state = this.localModel.getOwnPlayer() == null? ViewState.IN_LOBBY_CHOICE : ViewState.IN_LOBBY;
        Platform.runLater(() -> {
            this.navigator.showScene(this.state);
        });
    }

    @Override
    public void showNewPlayer() {
        if (this.state != ViewState.IN_LOBBY && this.state != ViewState.IN_LOBBY_CHOICE) return;
        Platform.runLater(() -> navigator.getCurrentScene().showNewPlayer());
    }

    @Override
    public void showGameStart() {
        this.state = ViewState.IN_GAME;
        Platform.runLater(() -> {
            this.navigator.showScene(this.state);
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo("Game started.");
        });
    }

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

    @Override
    public void handleLobbyJoinError(String message) {
        Platform.runLater(() -> {
            this.showPopUp("Lobby join failed: " + message);
            this.navigator.getCurrentScene().reset();
        });
    }

    @Override
    public void showGameError(String error) {
        this.localModel.stopValidation();
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showError(error);
        });
    }

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

    @Override
    public void showTotemPlaced(String nickname, int position) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " placed a totem in position " + (position + 1) + ".");
        });
    }

    @Override
    public void showCardPicked(String nickname, int cardId, boolean finalPick) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " picked " + this.cardName(cardId) + ".");
        });
    }

    @Override
    public void showBuildingAcquisition(String nickname, int cost) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " paid " + cost + " food to buy a building.");
        });
    }

    @Override
    public void showHunterEffect(String nickname, int food) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " received " + food + " food from a hunter.");
        });
    }

    @Override
    public void showBuildingEffect(String nickname, int bonus, String resource) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " received " + bonus + " " + resource + " from a building.");
        });
    }

    @Override
    public void showHuntEvent(Map<String, PointsPair> effects) {
        refreshGameSceneWithInfo("Hunt event resolved. " + this.formatFoodPrestigeEffects(effects));
    }

    @Override
    public void showPaintingEvent(Map<String, Integer> effects) {
        refreshGameSceneWithInfo("Painting event resolved. " + this.formatSingleResourceEffects(effects, "food"));
    }

    @Override
    public void showSustenanceEvent(Map<String, PointsPair> effects) {
        refreshGameSceneWithInfo("Sustenance event resolved. " + this.formatFoodPrestigeEffects(effects));
    }

    @Override
    public void showRitualEvent(Map<String, Integer> effects) {
        refreshGameSceneWithInfo("Ritual event resolved. " + this.formatSingleResourceEffects(effects, "prestige points"));
    }

    @Override
    public void showFinalPoints() {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            /*String winners = String.join(", ", this.localModel.getWinners());
            String message = "Game ended. Winners: " + winners + ".";
            this.navigator.getCurrentScene().showInfo(message);
            this.showGameEndDialog(winners);*/
        });
    }

    @Override
    public void showOrderModifier(String nickname, int modifier, boolean prestige) {
        if (this.state != ViewState.IN_GAME) return;
        String resource = prestige ? "prestige points" : "food";
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(nickname + " changed " + resource + " by " + modifier + ".");
        });
    }

    @Override
    public void showFoodOffer(String nickname) {
        refreshGameSceneWithInfo(nickname + " received 3 food from the offer card.");
    }

    @Override
    public void showRetrievedInfo() {
        Platform.runLater(this.navigator::showDisconnectionMenu);
    }

    @Override
    public void showDisconnectedPlayer(String nickname) {
        Platform.runLater(() -> this.navigator.getCurrentScene().showDisconnectedPlayer(nickname));
    }

    @Override
    public void showPlayerReconnection(String nickname) {
        Platform.runLater(() -> this.navigator.getCurrentScene().showPlayerReconnection(nickname));
    }

    @Override
    public void showDisconnection() {
        Platform.runLater(this.navigator::showLoading);
    }

    @Override
    public void enterLobbyChoice() {
        this.state = ViewState.LOBBY_CHOICE;
        Platform.runLater(() -> this.navigator.showScene(this.state));
    }

    @Override
    public void showGameEnd(List<RankElement> leaderboard, int myRank) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().showGameEnd(leaderboard, myRank);
        });
    }

    @Override
    public void showRoundEnding() {
    }

    private void refreshGameSceneWithInfo(String message) {
        if (this.state != ViewState.IN_GAME) return;
        Platform.runLater(() -> {
            this.navigator.getCurrentScene().refreshFromModel();
            this.navigator.getCurrentScene().showInfo(message);
        });
    }

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
                    .append(" prestige. ");
        });
        return builder.toString().trim();
    }

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
                .append(". "));
        return builder.toString().trim();
    }

    private String formatSigned(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private String cardName(int cardId) {
        return CardMetadataRegistry.get(cardId).displayName();
    }

    private void showGameEndDialog(String winners) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game ended");
        alert.setHeaderText(this.localModel.getWinners().contains(this.localModel.getOwnPlayer().getNickname()) ? "You won!" : "Game over");
        alert.setContentText("Winners: " + winners);
        alert.show();
    }

    public void showPopUp(String message) {
        this.popUp.show(message);
    }

    public void showMenu() {
        Platform.runLater(this.navigator::showMenu);
    }

}
