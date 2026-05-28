package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.model.enums.Color;

import java.util.Objects;

public record OrderQueueSlot(Color color, State state) {
    public enum State {
        EMPTY,
        ACTIVE,
        WAITING,
        DISCONNECTED
    }

    public OrderQueueSlot {
        Objects.requireNonNull(state);
        if (state != State.EMPTY) {
            Objects.requireNonNull(color);
        }
    }

    public static OrderQueueSlot empty() {
        return new OrderQueueSlot(null, State.EMPTY);
    }

    public static OrderQueueSlot active(Color color) {
        return new OrderQueueSlot(color, State.ACTIVE);
    }

    public static OrderQueueSlot waiting(Color color) {
        return new OrderQueueSlot(color, State.WAITING);
    }

    public static OrderQueueSlot disconnected(Color color) {
        return new OrderQueueSlot(color, State.DISCONNECTED);
    }

    public boolean emptySlot() {
        return this.state == State.EMPTY;
    }

    public boolean activeSlot() {
        return this.state == State.ACTIVE;
    }

    public boolean disconnectedSlot() {
        return this.state == State.DISCONNECTED;
    }
}
