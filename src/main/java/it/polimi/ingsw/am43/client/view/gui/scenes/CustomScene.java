package it.polimi.ingsw.am43.client.view.gui.scenes;

import it.polimi.ingsw.am43.client.PointsPair;
import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.client.view.gui.GUI;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.database.RankElement;
import javafx.fxml.FXML;

import java.util.List;
import java.util.Map;
/**
 * Abstract base class for all GUI scene controllers.
 * Implements the {@link UI} interface, providing default empty implementations
 * for view updates that concrete scenes can optionally override.
 */
public abstract class CustomScene implements UI {
    protected GUI gui;
    protected ClientController controller;

    /**
     * Handles the settings button action, shared across all scenes,
     * to display the game menu.
     */
    @FXML
    private void onSettingsClicked() {
        this.gui.submitTask(() -> this.gui.showMenu());
    }

    /**
     * Injects the main GUI instance.
     * @param gui the GUI manager
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Injects the client controller instance.
     * @param controller the client-side controller
     */
    public void setController(ClientController controller) {
        this.controller = controller;
    }

    public void refreshFromModel() {}

    public void showInfo(String s) {}

    public void showError(String s) {}

    public void reset() {}

    public void setUp() {}

    @Override
    public void showRetrievedInfo() {}

    @Override
    public void showDisconnectedPlayer(String nickname) {}

    @Override
    public void showDisconnection() {}

    @Override
    public void enterLobbyChoice() {}

    @Override
    public void showPlayerReconnection(String nickname) {}

    @Override
    public void showAvailableLobbies() {}

    @Override
    public void enterLobby() {}

    @Override
    public void showTimer(int length) {}

    @Override
    public void showNewPlayer() {}

    @Override
    public void showGameStart() {}

    @Override
    public void handleLobbyChoiceError(String message, boolean creation) {}

    @Override
    public void handleLobbyJoinError(String message) {}

    @Override
    public void showGameError(String error) {}

    @Override
    public void showNewCurrPlayer() {}

    @Override
    public void showTotemPlaced(String nickname, int position) {}

    @Override
    public void showCardPicked(String nickname, int cardId, boolean finalPick) {}

    @Override
    public void showBuildingAcquisition(String nickname, int cost) {}

    @Override
    public void showHunterEffect(String nickname, int food) {}

    @Override
    public void showBuildingEffect(String nickname, int bonus, String resource) {}

    @Override
    public void showHuntEvent(Map<String, PointsPair> effects) {}

    @Override
    public void showPaintingEvent(Map<String, Integer> effects) {}

    @Override
    public void showSustenanceEvent(Map<String, PointsPair> effects) {}

    @Override
    public void showRitualEvent(Map<String, Integer> effects) {}

    @Override
    public void showFinalPoints() {}

    @Override
    public void showOrderModifier(String nickname, int modifier, boolean prestige) {}

    @Override
    public void showFoodOffer(String nickname) {}

    @Override
    public void showGameEnd(List<RankElement> leaderboard, int myRank) {}

    @Override
    public void showRoundEnding() {}
}
