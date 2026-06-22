package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

/**
 * Character card that contributes shaman stars when added to a player's tribe.
 */
public class Shaman extends CharacterCard {
    private final int shamanStars;

    /**
     * Creates a {@code Shaman} card with the specified era, identifier, and shaman star value.
     *
     * @param era the era associated with the card
     * @param id the identifier of the card
     * @param shamanStars the number of shaman stars provided by this card
     */
    public Shaman(int era, int id, int shamanStars) {
        super(era, id);
        this.shamanStars = shamanStars;
    }

    /**
     * Returns the number of shaman stars provided by this card.
     *
     * @return the shaman star value
     */
    public int getShamanStars() {
        return shamanStars;
    }

    /**
     * Adds this shaman to the player's tribe, alters its shaman stars, and activates tribe buildings.
     *
     * @param observer the game observer used while activating tribe buildings
     * @param player the player whose tribe receives this card
     */
    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterShamanStars(shamanStars);
        player.getTribe().activateTribeBuildings(observer, player);
    }

}
