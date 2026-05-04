package it.polimi.ingsw.am43.model;

import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.Observer;

public class MockObserver implements GameObserver {
    @Override
    public void broadcast(Update update) {

    }
}
