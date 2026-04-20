package it.polimi.ingsw.am43.client.view;

import java.rmi.RemoteException;

public interface UI {
    void showMessage(String message);

    void lobbyUpdate();

    void enterLobby();

    void showPlayer();

    void showNewPlayer();

    void showStartedGame();
}
