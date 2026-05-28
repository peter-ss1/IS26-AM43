package it.polimi.ingsw.am43.client.view;

import it.polimi.ingsw.am43.client.PointsPair;
import it.polimi.ingsw.am43.database.RankElement;

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

    void showCardPicked(String nickname, int cardId, boolean finalPick);

    void showBuildingAcquisition(String nickname, int cost);

    void showHunterEffect(String nickname, int food);

    void showBuildingEffect(String nickname, int bonus, String resource);

    void showHuntEvent(Map<String, PointsPair> effects);

    void showPaintingEvent(Map<String, Integer> effects);

    void showSustenanceEvent(Map<String, PointsPair> effects);

    void showRitualEvent(Map<String, Integer> effects);

    void showFinalPoints();

    void showOrderModifier(String nickname, int modifier, boolean prestige);

    void showFoodOffer(String nickname);

    void showRetrievedInfo();

    void showDisconnectedPlayer(String nickname);

    void enterLobbyChoice();

    void showPlayerReconnection(String nickname);

    void showDisconnection();

    void showGameEnd(List<RankElement> leaderboard, int myRank);

    void showRoundEnding();

    void showTimer(int length);
}
