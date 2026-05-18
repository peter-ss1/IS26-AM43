package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.model.enums.Color;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class OrderQueueNode extends StackPane {
    private static final double ASSET_WIDTH = 118.0;
    private static final double DISCONNECTED_OPACITY = 0.55;

    private static final String IMAGE_PATH = "/it/polimi/ingsw/am43/images/OrderQueues/%d.png";
    private static final Map<Integer, List<SlotBounds>> SLOT_BOUNDS = Map.of(
            2, List.of(new SlotBounds(0.50, 0.29, 0.45, 0.12), new SlotBounds(0.50, 0.49, 0.45, 0.12)),
            3, List.of(new SlotBounds(0.50, 0.23, 0.44, 0.12), new SlotBounds(0.50, 0.43, 0.44, 0.12), new SlotBounds(0.50, 0.63, 0.44, 0.12)),
            4, List.of(new SlotBounds(0.50, 0.18, 0.45, 0.12), new SlotBounds(0.50, 0.38, 0.45, 0.12), new SlotBounds(0.50, 0.58, 0.45, 0.12), new SlotBounds(0.50, 0.78, 0.45, 0.12)),
            5, List.of(new SlotBounds(0.50, 0.12, 0.43, 0.12), new SlotBounds(0.50, 0.31, 0.43, 0.12), new SlotBounds(0.50, 0.50, 0.43, 0.12), new SlotBounds(0.50, 0.70, 0.43, 0.12), new SlotBounds(0.50, 0.91, 0.43, 0.12))
    );

    private static final ResourceImageCache<Integer> IMAGE_CACHE = new ResourceImageCache<>();

    public OrderQueueNode(int numPlayers, List<OrderQueueSlot> orderQueueSlots, Predicate<Color> highlightPredicate,
                          BiConsumer<TotemNode, Color> dragConfigurator) {
        Image image = IMAGE_CACHE.get(numPlayers, id -> String.format(IMAGE_PATH, id));
        if (image == null) {
            this.renderFallback(orderQueueSlots, highlightPredicate, dragConfigurator);
        } else {
            this.renderImageQueue(numPlayers, orderQueueSlots, highlightPredicate, dragConfigurator, image);
        }
    }

    private void renderImageQueue(int numPlayers, List<OrderQueueSlot> orderQueueSlots, Predicate<Color> highlightPredicate,
                                  BiConsumer<TotemNode, Color> dragConfigurator, Image image) {
        double imageHeight = ASSET_WIDTH * image.getHeight() / image.getWidth();
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(ASSET_WIDTH);
        imageView.setFitHeight(imageHeight);
        imageView.setPreserveRatio(true);

        AnchorPane overlay = new AnchorPane();
        overlay.setMinSize(ASSET_WIDTH, imageHeight);
        overlay.setPrefSize(ASSET_WIDTH, imageHeight);
        overlay.setMaxSize(ASSET_WIDTH, imageHeight);

        List<SlotBounds> slots = SLOT_BOUNDS.getOrDefault(numPlayers, List.of());
        for (int i = 0; i < orderQueueSlots.size() && i < slots.size(); i++) {
            OrderQueueSlot slot = orderQueueSlots.get(i);
            if (slot.isEmpty()) {
                continue;
            }
            Color color = slot.color();
            TotemNode totem = slots.get(i).createTotemNode(color, ASSET_WIDTH, imageHeight);
            this.configureTotem(slot, totem, highlightPredicate, dragConfigurator);
            overlay.getChildren().add(totem);
        }

        this.getChildren().addAll(imageView, overlay);
    }

    private void renderFallback(List<OrderQueueSlot> orderQueueSlots, Predicate<Color> highlightPredicate,
                                BiConsumer<TotemNode, Color> dragConfigurator) {
        HBox fallback = new HBox(6);
        fallback.setAlignment(Pos.CENTER);
        for (OrderQueueSlot slot : orderQueueSlots) {
            if (slot.isEmpty()) {
                Region emptySlot = new Region();
                emptySlot.setPrefSize(22.0, 22.0);
                fallback.getChildren().add(emptySlot);
                continue;
            }
            Color color = slot.color();
            TotemNode totem = new TotemNode(color, 22.0);
            this.configureTotem(slot, totem, highlightPredicate, dragConfigurator);
            fallback.getChildren().add(totem);
        }
        this.getChildren().add(fallback);
    }

    private void configureTotem(OrderQueueSlot slot, TotemNode totem, Predicate<Color> highlightPredicate,
                                BiConsumer<TotemNode, Color> dragConfigurator) {
        Color color = slot.color();
        totem.setHighlighted(highlightPredicate.test(color));
        if (slot.disconnectedSlot()) {
            totem.setOpacity(DISCONNECTED_OPACITY);
            totem.setDraggable(false);
            return;
        }
        if (slot.activeSlot()) {
            dragConfigurator.accept(totem, color);
        } else {
            totem.setDraggable(false);
        }
    }
}
