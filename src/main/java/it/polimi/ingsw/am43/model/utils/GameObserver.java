package it.polimi.ingsw.am43.model.utils;

import it.polimi.ingsw.am43.network.message.Update;

/**
 * Interface defining the observer pattern used by the game controller to
 * monitoring state changes within the game model.
 */
public interface GameObserver {
    /**
     * Notifies the observer of an update to send to every active connected client.
     *
     * @param update the data object to dispatch
     */
    void broadcast(Update update);

    /**
     * Notifies the observer of an isolated update to send to one specified client.
     *
     * @param name   the player name
     * @param update the data object to dispatch
     */
    void updatePlayer(String name, Update update);

    /**
     * Notifies the observer of the conclusion of the game.
     */
    void notifyEndGame();

    /**
     * Notifies the observer that the game contains a single active player.
     *
     * @param name nickname of the remaining player
     */
    void notifySinglePlayerGame(String name);
}
