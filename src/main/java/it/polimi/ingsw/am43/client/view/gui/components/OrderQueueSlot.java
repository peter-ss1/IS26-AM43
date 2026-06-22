package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.model.enums.Color;

import java.util.Objects;

/**
 * Immutable representation of a visual slot in the order queue.
 *
 * @param color the color associated with the slot, or {@code null} for an empty slot
 * @param state the visual state of the slot
 */
public record OrderQueueSlot(Color color, State state) {
    /**
     * Visual states available for an order queue slot.
     */
    public enum State {
        EMPTY,
        ACTIVE,
        WAITING,
        DISCONNECTED
    }

    /**
     * Creates an order queue slot with the specified color and state.
     *
     * @param color the color associated with the slot
     * @param state the visual state of the slot
     */
    public OrderQueueSlot {
        Objects.requireNonNull(state);
        if (state != State.EMPTY) {
            Objects.requireNonNull(color);
        }
    }

    /**
     * Creates an empty order queue slot.
     *
     * @return an empty order queue slot
     */
    public static OrderQueueSlot empty() {
        return new OrderQueueSlot(null, State.EMPTY);
    }

    /**
     * Creates an active order queue slot for the specified color.
     *
     * @param color the color associated with the active slot
     * @return an active order queue slot
     */
    public static OrderQueueSlot active(Color color) {
        return new OrderQueueSlot(color, State.ACTIVE);
    }

    /**
     * Creates a waiting order queue slot for the specified color.
     *
     * @param color the color associated with the waiting slot
     * @return a waiting order queue slot
     */
    public static OrderQueueSlot waiting(Color color) {
        return new OrderQueueSlot(color, State.WAITING);
    }

    /**
     * Creates a disconnected order queue slot for the specified color.
     *
     * @param color the color associated with the disconnected slot
     * @return a disconnected order queue slot
     */
    public static OrderQueueSlot disconnected(Color color) {
        return new OrderQueueSlot(color, State.DISCONNECTED);
    }

    /**
     * Returns whether this slot is empty.
     *
     * @return {@code true} if this slot is empty, {@code false} otherwise
     */
    public boolean emptySlot() {
        return this.state == State.EMPTY;
    }

    /**
     * Returns whether this slot is active.
     *
     * @return {@code true} if this slot is active, {@code false} otherwise
     */
    public boolean activeSlot() {
        return this.state == State.ACTIVE;
    }

    /**
     * Returns whether this slot is disconnected.
     *
     * @return {@code true} if this slot is disconnected, {@code false} otherwise
     */
    public boolean disconnectedSlot() {
        return this.state == State.DISCONNECTED;
    }
}
