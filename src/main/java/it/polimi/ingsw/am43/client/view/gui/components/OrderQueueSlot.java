package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.model.enums.Color;

public record OrderQueueSlot(Color color) {
    public static OrderQueueSlot empty() {
        return new OrderQueueSlot(null);
    }

    public static OrderQueueSlot active(Color color) {
        return new OrderQueueSlot(color);
    }

    public boolean isEmpty() {
        return this.color == null;
    }
}
