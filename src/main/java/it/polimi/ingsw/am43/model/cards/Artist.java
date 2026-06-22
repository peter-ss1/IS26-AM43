package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

/**
 * Character card that applies the artist-specific tribe entrance effect.
 */
public class Artist extends CharacterCard {

    /**
     * Creates an {@code Artist} card with the specified era and identifier.
     *
     * @param era the era associated with the card
     * @param id the identifier of the card
     */
    public Artist(int era, int id) {
        super(era, id);
    }

    /**
     * Adds this card to the player's tribe and activates the tribe buildings.
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