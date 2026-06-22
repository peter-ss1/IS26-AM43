package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

import java.io.Serializable;

/**
 * Abstract class representing a generic card within the game.
 * Outlines high-level shared properties such as eras,
 * unique identifiers, and pickup behaviors.
 */
public abstract class Card implements Serializable {
    private final int era;
    private final int id;

    public Card(int era, int id) {
        this.era = era;
        this.id = id;
    }

    /** @return the unique id of the card */
    public int getId() {
        return id;
    }

    /** @return the era (1, 2 or 3) the card belongs to */
    public int getEra() {
        return era;
    }

    /** @return true if the card can be picked up by a player (false for Events) */
    public abstract boolean isPickable();

    /**
     * Adds the card to the given board row.
     *
     * @param row the row to add the card to
     */
    public abstract void addToRow(Row row);

    /** @return the row (TOP or BOTTOM) where the card must be placed at the start of the game */
    public abstract OfferAction firstRowChoice();
    /**
     * Applies the card's pickup effect and notifies the observer.
     * The card itself sends the notification — Game does not broadcast directly.
     *
     * @param observer the controller that will receive the notification
     * @param player   the player who picked the card
     */
    public abstract void pick(GameObserver observer, Player player);

    /** @return true if the card is located in the given row */
    public abstract boolean isContainedInRow(Row row);
    /**
     * Removes the card from the given row.
     *
     * @param row the row to remove the card from
     */
    public abstract void removeFromRow(Row row);
}