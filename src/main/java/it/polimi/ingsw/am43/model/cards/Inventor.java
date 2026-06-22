package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

/**
 * Character card identified by an inventor symbol.
 */
public class Inventor extends CharacterCard {
    private final InventorSymbol symbol;

    /**
     * Creates an {@code Inventor} card with the specified era, identifier, and symbol.
     *
     * @param era the era associated with the card
     * @param id the identifier of the card
     * @param symbol the inventor symbol associated with this card
     */
    public Inventor(int era, int id, InventorSymbol symbol) {
        super(era, id);
        this.symbol = symbol;
    }

    /**
     * Returns the inventor symbol associated with this card.
     *
     * @return the inventor symbol
     */
    public InventorSymbol getSymbol() {
        return symbol;
    }

    /**
     * Adds this inventor to the player's tribe and activates tribe buildings.
     *
     * @param observer the game observer used while activating tribe buildings
     * @param player the player whose tribe receives this card
     */
    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.getTribe().activateTribeBuildings(observer, player);
    }

}
