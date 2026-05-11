package it.polimi.ingsw.am43.model.utils;

import it.polimi.ingsw.am43.network.message.Update;

public interface GameObserver {
    void broadcast(Update update);
    void updatePlayer(String name, Update update);
}
