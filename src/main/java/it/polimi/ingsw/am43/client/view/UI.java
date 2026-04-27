package it.polimi.ingsw.am43.client.view;

public interface UI {
    void showMessage(String message);

    void showAvailableLobbies();

    void enterLobby();

    void showNewPlayer();

    void showGameStart();

    void handleLobbyChoiceError(String message, boolean creation);

    void handleLobbyJoinError(String message);

    void showError(String s);

    void showNewCurrPlayer();

    void showTotemPlaced(String nickname, int position);

    void showCardPicked(String nickname, int cardId);
}
