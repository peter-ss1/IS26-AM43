package it.polimi.ingsw.am43.model;

import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

public class MockObserver implements GameObserver {
    @Override
    public void broadcast(Update update) {
    }

    @Override
    public void endGame() {
    }

    @Override
    public void notifySinglePlayerGame(String name) {
    }

    @Override
    public void updatePlayer(String name, Update update) {
    }
}
