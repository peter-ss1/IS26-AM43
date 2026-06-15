package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

/**
 * Character card that contributes a building discount and end-game prestige points.
 */
public class Builder extends CharacterCard {
    private final int buildingDiscount;
    private final int prestigePoints;

    /**
     * Creates a {@code Builder} card with the specified era, identifier, discount, and prestige value.
     *
     * @param era the era associated with the card
     * @param id the identifier of the card
     * @param buildingDiscount the discount this card adds to the player's building discount
     * @param prestigePoints the prestige points associated with this builder
     */
    public Builder(int era, int id, int buildingDiscount, int prestigePoints) {
        super(era, id);
        this.buildingDiscount = buildingDiscount;
        this.prestigePoints = prestigePoints;
    }

    /**
     * Returns the building discount provided by this card.
     *
     * @return the building discount value
     */
    public int getBuildingDiscount() {
        return buildingDiscount;
    }

    /**
     * Returns the prestige points associated with this builder.
     *
     * @return the prestige point value
     */
    public int getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * Adds this builder to the player's tribe, updates the counter that allows the player to purchase a building at a discounted price, and activates tribe buildings.
     *
     * @param observer the game observer used while activating tribe buildings
     * @param player the player whose tribe receives this card
     */
    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterBuildingDiscount(buildingDiscount);
        player.getTribe().activateTribeBuildings(observer, player);
    }
}
