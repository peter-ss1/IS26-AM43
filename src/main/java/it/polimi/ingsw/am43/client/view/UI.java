package it.polimi.ingsw.am43.client.view;

import java.util.List;
import java.util.Map;

public interface UI {

    void showAvailableLobbies();

    void enterLobby();

    void showNewPlayer();

    void showGameStart();

    void handleLobbyChoiceError(String message, boolean creation);

    void handleLobbyJoinError(String message);

    void showGameError(String error);

    void showNewCurrPlayer();

    void showTotemPlaced(String nickname, int position);

    void showCardPicked(String nickname, int cardId);

    void showBuildingAcquisition(String nickname, int cost);

    void showHunterEffect(String nickname, int food);

    void showBuildingEffect(String nickname, int bonus, String resource);

    void showHuntEvent(Map<String, List<Integer>> effects);

    void showPaintingEvent(Map<String, Integer> effects);

    void showSustenanceEvent(Map<String, List<Integer>> effects);

    void showRitualEvent(Map<String, Integer> effects);

    void showGameEnd();

    void showOrderModifier(String nickname, int modifier, boolean prestige);

    void showFoodOffer(String nickname);

    void showRetrievedInfo();

    void showDisconnectedPlayer(String nickname);

    void enterLobbyChoice();

    void showPlayerReconnection(String nickname);

    void showDisconnection();
    void showLeaderboard(List<String> leaderboard, int myRank, int numGiocatori);
}
