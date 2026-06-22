package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

import java.util.List;

/**
 * Base class for every event card. Events cannot be picked: they sit in the top
 * row and are resolved automatically, in priority order, affecting the players.
 */
public abstract class Event extends Card implements Comparable<Event> {

    private final int resolutionPriority;

    /**
     * @param era                the era the event belongs to
     * @param id                 the unique id of the event
     * @param resolutionPriority the priority that determines the resolution order
     */
    public Event(int era, int id, int resolutionPriority) {
        super(era, id);
        this.resolutionPriority=resolutionPriority;
    }


    /**
     * Compares two events to determine their resolution order, based on priority
     * and, as a tiebreaker, on the era.
     *
     * @param event the event to compare against
     * @return a negative, zero or positive value as per {@link Comparable}
     */
    @Override
    public int compareTo(Event event) {
        if (Integer.compare(this.resolutionPriority, event.resolutionPriority) != 0) {
            return 1;
        }
        return Integer.compare(this.getEra(), event.getEra());
    }

    /** {@inheritDoc} Events are never pickable. */
    @Override
    public boolean isPickable() {
        return false;
    }

    /** {@inheritDoc} Adds this event to the given row. */
    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    /** {@inheritDoc} Events are placed in the top row. */
    @Override
    public OfferAction firstRowChoice() {
        return OfferAction.TOP;
    }

    /**
     * Applies this event's effect to the given players.
     *
     * @param observer the observer to notify of the effects
     * @param players  the players affected by the event
     */
    public abstract void affectPlayers(GameObserver observer , List<Player> players);

    /**
     * Triggers the matching event building when this event is resolved.
     *
     * @param observer the observer to notify
     * @param building the event building to trigger
     * @param player   the owner of the building
     */
    public abstract void triggerBuilding(GameObserver observer, EventBuilding building, Player player);

    /**
     * {@inheritDoc}
     * @throws IllegalMoveException always, since event cards cannot be picked
     */
    @Override
    public void pick(GameObserver observer, Player player) {
        throw new IllegalMoveException("Cannot pick event card");
    }

    /** {@inheritDoc} */
    @Override
    public boolean isContainedInRow(Row row) {
        return row.contains(this);
    }

    /** {@inheritDoc} */
    @Override
    public void removeFromRow(Row row) {
        row.removeCard(this);
    }

}
