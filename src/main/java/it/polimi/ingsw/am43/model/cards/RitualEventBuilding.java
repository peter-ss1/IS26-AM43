package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

/**
 * Event building that reacts to a {@link RitualEvent} according to the
 * {@link EventEffect} it carries.
 */
public class RitualEventBuilding extends EventBuilding {
    /** The effect applied whenever a ritual event is resolved. */
    public final EventEffect<RitualEvent> reaction;

    /**
     * @param era            the era the building belongs to
     * @param id             the unique id of the building
     * @param cost           the food cost to pick the building
     * @param prestigePoints the prestige points the building grants
     * @param reaction       the effect triggered on a ritual event
     */
    public RitualEventBuilding(int era,int id, int cost, int prestigePoints, EventEffect<RitualEvent> reaction) {
        super(era, id,cost, prestigePoints);
        this.reaction = reaction;
    }

    /** {@inheritDoc} */
    @Override
    public void reactToEvent(GameObserver observer, Player player, RitualEvent event) {
        reaction.manifest(observer, player, event);
    }
}
