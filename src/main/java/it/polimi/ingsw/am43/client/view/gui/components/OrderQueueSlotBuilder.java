package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.model.enums.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that creates visual order queue slots from client-side queue state.
 */
public final class OrderQueueSlotBuilder {
    /**
     * Builds the visual slots used to render the order queue.
     *
     * @param numPlayers the number of players in the game
     * @param orderQueue the active colors currently in the order queue
     * @param disconnectedPlayers the colors of disconnected players
     * @param waitingPlayers the colors of players waiting outside the active queue
     * @param top {@code true} to place empty slots after active colors, {@code false} to place them before
     * @return the visual order queue slots
     */
    public static List<OrderQueueSlot> buildSlots(int numPlayers, List<Color> orderQueue,
                                                  List<Color> disconnectedPlayers,
                                                  List<Color> waitingPlayers,
                                                  boolean top) {
        List<OrderQueueSlot> slots = new ArrayList<>();

        if (!top) {
            addEmptySlots(slots, numPlayers - (orderQueue.size() + waitingPlayers.size() + disconnectedPlayers.size()));
        }

        orderQueue.stream()
                .map(OrderQueueSlot::active)
                .forEach(slots::add);

        if (top) {
            addEmptySlots(slots, numPlayers - (waitingPlayers.size() + disconnectedPlayers.size()) - slots.size());
        }

        waitingPlayers.stream()
                .map(OrderQueueSlot::waiting)
                .forEach(slots::add);

        disconnectedPlayers.stream()
                .map(OrderQueueSlot::disconnected)
                .forEach(slots::add);

        return slots.stream()
                .limit(Math.max(0, numPlayers))
                .toList();
    }

    private static void addEmptySlots(List<OrderQueueSlot> slots, int count) {
        for (int i = 0; i < count; i++) {
            slots.add(OrderQueueSlot.empty());
        }
    }
}
