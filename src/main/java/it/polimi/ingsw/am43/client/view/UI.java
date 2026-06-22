package it.polimi.ingsw.am43.client.view;

import it.polimi.ingsw.am43.client.PointsPair;
import it.polimi.ingsw.am43.database.RankElement;

import java.util.List;
import java.util.Map;

/**
 * The generalized View interface for the client-side application.
 * Defines the updates that the local model and controller can display
 * in the User Interface, without distinction between GUI and TUI.
 */
public interface UI {

    /**
     * Updates the view to display or refresh the list of available game lobbies on the server.
     */
    void showAvailableLobbies();

    /**
     * Transitions the view into the pre-game lobby screen.
     */
    void enterLobby();

    /**
     * Notifies the view that a new player has registered or joined the current lobby.
     */
    void showNewPlayer();

    /**
     * Transitions the view into the main gameplay scene.
     */
    void showGameStart();

    /**
     * Reports an error encountered during an interactive lobby choice action.
     *
     * @param message  The descriptive error explanation.
     * @param creation {@code true} if the error occurred during lobby creation,
     * {@code false} if it occurred while trying to join.
     */
    void handleLobbyChoiceError(String message, boolean creation);

    /**
     * Reports an error encountered while joining a lobby.
     *
     * @param message The descriptive error explanation.
     */
    void handleLobbyJoinError(String message);

    /**
     * Reports a gameplay exception, rule violation, or illegal move alert to the user.
     *
     * @param error The descriptive error explanation.
     */
    void showGameError(String error);

    /**
     * Notifies the view that the active turn has shifted to a new player.
     */
    void showNewCurrPlayer();

    /**
     * Notifies the view that a specific player has positioned a totem.
     *
     * @param nickname The player who performed the action.
     * @param position The zero-indexed position of totem placement.
     */
    void showTotemPlaced(String nickname, int position);

    /**
     * Notifies the view that a specific player has picked a card.
     *
     * @param nickname The player who performed the action.
     * @param cardId    The numeric identifier of the picked card.
     * @param finalPick {@code true} if this action concludes the player's turn.
     */
    void showCardPicked(String nickname, int cardId, boolean finalPick);

    /**
     * Displays the effects of a building acquisition.
     *
     * @param nickname The affected player.
     * @param cost     The amount of food consumed.
     */
    void showBuildingAcquisition(String nickname, int cost);

    /**
     * Displays the effects of an active hunter acquisition.
     *
     * @param nickname The affected player.
     * @param food     The amount of food procured.
     */
    void showHunterEffect(String nickname, int food);

    /**
     * Displays the effects of a building activation.
     *
     * @param nickname The affected player.
     * @param bonus    The numerical amount of resource involved.
     * @param resource The type of resource involved.
     */
    void showBuildingEffect(String nickname, int bonus, String resource);

    /**
     * Displays the effects of a hunt event resolution.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     * in food and prestige points.
     */
    void showHuntEvent(Map<String, PointsPair> effects);

    /**
     * Displays the effects of a painting event resolution.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     * in food.
     */
    void showPaintingEvent(Map<String, Integer> effects);

    /**
     * Displays the effects of a sustenance event resolution.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     * in food and prestige points.
     */
    void showSustenanceEvent(Map<String, PointsPair> effects);

    /**
     * Displays the effects of a sustenance event resolution.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     * in prestige points.
     */
    void showRitualEvent(Map<String, Integer> effects);

    /**
     * Notifies the view that the final points have been updated.
     */
    void showFinalPoints();

    /**
     * Displays the effects of a modifier given based on next turn order.
     *
     * @param nickname The affected player.
     * @param modifier The involved bonus/malus.
     * @param prestige {@code true} if the change alters prestige points,
     * {@code false} if it applies to food.
     */
    void showOrderModifier(String nickname, int modifier, boolean prestige);

    /**
     * Displays the effects of the food granting offer track card.
     *
     * @param nickname The affected player.
     */
    void showFoodOffer(String nickname);

    /**
     * Notifies the view that a previous client configuration has been found and the client must choose an action.
     */
    void showRetrievedInfo();

    /**
     * Reports the disconnection of a player from the game.
     *
     * @param nickname The disconnected player.
     */
    void showDisconnectedPlayer(String nickname);

    /**
     * Transitions the view into the lobby selection scene.
     */
    void enterLobbyChoice();

    /**
     * Reports the reconnection of a player to the game.
     *
     * @param nickname The reconnected player.
     */
    void showPlayerReconnection(String nickname);

    /**
     * Reports the loss of connection with the server, transitioning into a waiting state.
     */
    void showDisconnection();

    /**
     * Displays the match outcomes with global historical rankings.
     *
     * @param leaderboard A sorted list with global historical ranking info.
     * @param myRank      The client position within global historical ranking.
     */
    void showGameEnd(List<RankElement> leaderboard, int myRank);

    /**
     * Notifies the view that the current round has ended.
     */
    void showRoundEnding();

    /**
     * Displays an active game termination countdown timer to a solitary remaining player.
     *
     * @param length The duration of the timer in seconds.
     */
    void showTimer(int length);
}