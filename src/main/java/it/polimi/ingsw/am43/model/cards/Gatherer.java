package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

/**
 * Character card that increases the player's sustenance discount during sustenance events when added to a tribe.
 */
public class Gatherer extends CharacterCard {

    /**
     * Creates a {@code Gatherer} card with the specified era and identifier.
     *
     * @param era the era associated with the card
     * @param id the identifier of the card
     */
    public Gatherer(int era, int id) {
        super(era, id);
    }

    /**
     * Adds this gatherer to the player's tribe, increases the sustenance discount, and activates tribe buildings.
     *
     * @param observer the game observer used while activating tribe buildings
     * @param player the player whose tribe receives this card
     */
    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterSustenanceDiscount(3);
        player.getTribe().activateTribeBuildings(observer, player);
    }
}
