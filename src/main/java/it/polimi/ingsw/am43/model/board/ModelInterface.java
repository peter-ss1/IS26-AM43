package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

import java.io.Serializable;
import java.util.List;

/**
 * Public business logic contract exposed by the model to the controller.
 * It only allows player-related state changes and connection handling notifiers.
 */
public interface ModelInterface extends Serializable {

    /** Marks the player as disconnected (INACTIVE) and advances the turn if needed. */
    void moveToInactive(Player player);

    /** Re-admits an INACTIVE player as a spectator (WAITING), sending them the current snapshot. */
    void moveToWait(Player player);

    /** @return the maximum number of players in the game */
    int getNumPlayers();

    /**
     * Adds a player to the game during the setup phase.
     *
     * @param nickname the chosen nickname
     * @param color    the chosen color
     */
    void addPlayer(String nickname, Color color);

    /** Removes a player from the game before it starts. */
    void removePlayer(String nickname);

    /**
     * Places the player's totem on the offer track.
     *
     * @param player   the player
     * @param position the chosen position (0-based)
     */
    void placeTotemOnTrack(Player player, int position);

    /**
     * @param name the player's nickname
     * @return the matching player
     */
    Player getPlayerByName(String name);

    /**
     * @param id the unique id of the card
     * @return the matching card
     */
    Card getCardById(int id);

    /**
     * The player picks the given card from the board.
     *
     * @param card   the card to pick
     * @param player the player picking it
     */
    void pickCard(Card card, Player player);

    /** Ends the current player's turn. */
    void endCurrentTurn(Player player);

    /** @return the colors still available for the player to choose */
    List<Color> getAvailableColors();

    /**
     * Attaches the GameObserver to the model. Required after every restore from disk
     * since the field is transient.
     *
     * @param observer the controller to notify
     */
    void setObserver(GameObserver observer);

    /** @return all players registered in the game */
    List<Player> getAllPlayers();

    /** Starts the game, resolves the initial phase and sends the start update to all clients. */
    void startGame();

    /** Sends the full game snapshot to the player who is reconnecting. */
    void restartGame();
}
