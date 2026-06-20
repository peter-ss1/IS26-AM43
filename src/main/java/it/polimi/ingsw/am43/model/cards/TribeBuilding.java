package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

/**
 * A building that grants a recurring {@link TribeBonus}. It remembers the last
 * bonus granted so that only the delta is applied on subsequent evaluations.
 */
public class TribeBuilding extends Building {
    private final TribeBonus bonus;
    private int lastGivenBonus;

    /**
     * @param era            the era the building belongs to
     * @param id             the unique id of the building
     * @param cost           the food cost to pick the building
     * @param prestigePoints the prestige points the building grants
     * @param bonus          the recurring bonus granted by the building
     */
    public TribeBuilding(int era, int id, int cost, int prestigePoints, TribeBonus bonus) {
        super(era, id, cost, prestigePoints);
        this.bonus = bonus;
        this.lastGivenBonus = 0;
    }

    /**
     * {@inheritDoc}
     * Adds the building to the tribe, charges its cost (after the building
     * discount), records the initial bonus value and re-evaluates all tribe
     * buildings.
     */
    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        int cost = this.getCost() - player.getBuildingDiscount();
        if (cost<0) cost = 0;
        player.alterFood(-cost);
        lastGivenBonus = this.bonus.calculateBonus(player);
        observer.broadcast(new Update.BuildingBoughtUpdate(player.getNickname(), cost));
        player.getTribe().activateTribeBuildings(observer, player);
    }

    /**
     * Recomputes the bonus and applies only the difference with respect to the
     * previously granted value.
     *
     * @param observer the observer to notify
     * @param player   the owner of the building
     */
    public void tribeBuildingEffect(GameObserver observer, Player player) {
        int newBonus = this.bonus.calculateBonus(player);
        if (newBonus != this.lastGivenBonus) bonus.giveBonus(observer, player, newBonus - this.lastGivenBonus);
        this.lastGivenBonus = newBonus;
    }
}
