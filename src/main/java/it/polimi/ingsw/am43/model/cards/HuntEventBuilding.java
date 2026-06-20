package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

/**
 * Event building that reacts to a {@link HuntEvent} according to the
 * {@link EventEffect} it carries.
 */
public class HuntEventBuilding extends EventBuilding {
    /** The effect applied whenever a hunt event is resolved. */
    public final EventEffect<HuntEvent> reaction;

    /**
     * @param era            the era the building belongs to
     * @param id             the unique id of the building
     * @param cost           the food cost to pick the building
     * @param prestigePoints the prestige points the building grants
     * @param reaction       the effect triggered on a hunt event
     */
    public HuntEventBuilding(int era, int id, int cost, int prestigePoints, EventEffect<HuntEvent> reaction) {
        super(era, id, cost, prestigePoints);
        this.reaction = reaction;
    }

    /** {@inheritDoc} */
    @Override
    public void reactToEvent(GameObserver observer, Player player, HuntEvent event) {
        reaction.manifest(observer, player, event);
    }
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Edificio Evento Caccia (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Reagisce quando si risolve un Evento Caccia.";
    }
}
