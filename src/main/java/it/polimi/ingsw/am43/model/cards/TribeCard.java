package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

/**
 * Base class for the cards a player can pick into their tribe (characters and
 * buildings). Every tribe card is pickable and starts in the bottom row.
 */
public abstract class TribeCard extends Card {

    /**
     * @param era the era the card belongs to
     * @param id  the unique id of the card
     */
    public TribeCard(int era, int id) {
        super(era, id);
    }

    /** {@inheritDoc} Tribe cards are always pickable. */
    @Override
    public boolean isPickable() {
        return true;
    }

    /**
     * Applies the effect triggered when this card enters a player's tribe.
     *
     * @param observer the observer to notify
     * @param player   the player whose tribe the card enters
     */
    public abstract void tribeEntranceEffect(GameObserver observer, Player player);

    /** {@inheritDoc} Tribe cards are placed in the bottom row. */
    @Override
    public OfferAction firstRowChoice() {
        return OfferAction.BOTTOM;
    }
}
