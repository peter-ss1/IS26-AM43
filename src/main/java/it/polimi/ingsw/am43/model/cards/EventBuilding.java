package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

/**
 * Base class for buildings that react to events. Subclasses override the
 * {@code reactToEvent} overload matching the event type they care about;
 * the others are no-ops by default.
 */
public abstract class EventBuilding extends Building {

    /**
     * @param era            the era the building belongs to
     * @param id             the unique id of the building
     * @param cost           the food cost to pick the building
     * @param prestigePoints the prestige points the building grants
     */
    public EventBuilding(int era, int id, int cost, int prestigePoints) {
        super(era,id, cost, prestigePoints);
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
     * Reacts to a sustenance event. No-op by default; overridden by subclasses that care.
     *
     * @param observer the observer to notify
     * @param player   the owner of the building
     * @param event    the sustenance event being resolved
     */
    public void reactToEvent(GameObserver observer, Player player, SustenanceEvent event) {
    }

    /**
     * Reacts to a hunt event. No-op by default; overridden by subclasses that care.
     *
     * @param observer the observer to notify
     * @param player   the owner of the building
     * @param event    the hunt event being resolved
     */
    public void reactToEvent(GameObserver observer, Player player, HuntEvent event) {
    }

    /**
     * Reacts to a painting event. No-op by default; overridden by subclasses that care.
     *
     * @param observer the observer to notify
     * @param player   the owner of the building
     * @param event    the painting event being resolved
     */
    public void reactToEvent(GameObserver observer, Player player, PaintingEvent event) {
    }

    /**
     * Reacts to a ritual event. No-op by default; overridden by subclasses that care.
     *
     * @param observer the observer to notify
     * @param player   the owner of the building
     * @param event    the ritual event being resolved
     */
    public void reactToEvent(GameObserver observer, Player player, RitualEvent event) {
    }
}
