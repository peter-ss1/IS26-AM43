package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

/**
 * A building whose effect is applied once, at the end of the game,
 * according to the {@link FinalEffect} strategy it carries.
 */
public class FinalBuilding extends Building {
    private final FinalEffect effect;

    /**
     * @param era            the era the building belongs to
     * @param id             the unique id of the building
     * @param cost           the food cost to pick the building
     * @param prestigePoints the prestige points the building grants
     * @param effect         the effect applied at the end of the game
     */
    public FinalBuilding(int era, int id, int cost, int prestigePoints, FinalEffect effect) {
        super(era, id, cost, prestigePoints);
        this.effect = effect;
    }

    /**
     * {@inheritDoc}
     * Adds the building to the tribe and charges its cost (after the building discount).
     */
    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        int cost = this.getCost() - player.getBuildingDiscount();
        if (cost<0) cost = 0;
        player.alterFood(-cost);
        observer.broadcast(new Update.BuildingBoughtUpdate(player.getNickname(), cost));
    }

    /**
     * Applies the building's end-game effect to its owner.
     *
     * @param player the owner of the building
     */
    public void finalBuildingEffect(Player player) {
        this.effect.manifest(player);
    }
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Edificio Finale (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Attiva il suo effetto alla fine della partita.";
    }

}
