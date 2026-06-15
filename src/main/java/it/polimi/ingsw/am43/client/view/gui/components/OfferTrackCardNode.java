package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * JavaFX node that displays an offer track slot with its actions and optional totem.
 */
public class OfferTrackCardNode extends StackPane {
    private static final double ASSET_WIDTH = 118.0;

    private static final String IMAGE_PATH = "/it/polimi/ingsw/am43/images/OfferTrackCards/%s.png";
    private static final SlotBounds TOTEM_BOUNDS = new SlotBounds(0.50, 0.25, 0.46, 0.13);
    private static final Map<List<OfferAction>, String> ASSETS = Map.of(
            List.of(OfferAction.FOOD), "A",
            List.of(OfferAction.BOTTOM), "B",
            List.of(OfferAction.TOP), "C",
            List.of(OfferAction.BOTTOM, OfferAction.BOTTOM), "D",
            List.of(OfferAction.BOTTOM, OfferAction.TOP), "E",
            List.of(OfferAction.TOP, OfferAction.TOP), "F",
            List.of(OfferAction.BOTTOM, OfferAction.TOP, OfferAction.TOP), "G"
    );
    private static final ResourceImageCache<String> IMAGE_CACHE = new ResourceImageCache<>();

    /**
     * Creates a node for the specified offer track slot.
     *
     * @param index the zero-based position of the slot in the offer track
     * @param slot the offer track slot to display
     * @param highlightPredicate predicate used to determine whether the slot totem is highlighted
     */
    public OfferTrackCardNode(int index, OfferTrackElement slot, Predicate<Color> highlightPredicate) {
        String assetName = ASSETS.get(slot.getOfferActions());
        Image image = assetName == null ? null : IMAGE_CACHE.get(assetName, name -> String.format(IMAGE_PATH, name));
        Node content = image == null
                ? this.createFallback(index, slot, highlightPredicate)
                : this.createImageContent(slot, highlightPredicate, image);
        this.getChildren().add(content);
    }

    private Node createImageContent(OfferTrackElement slot, Predicate<Color> highlightPredicate, Image image) {
        double imageHeight = ASSET_WIDTH * image.getHeight() / image.getWidth();
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(ASSET_WIDTH);
        imageView.setFitHeight(imageHeight);
        imageView.setPreserveRatio(true);

        AnchorPane overlay = new AnchorPane();
        overlay.setMinSize(ASSET_WIDTH, imageHeight);
        overlay.setPrefSize(ASSET_WIDTH, imageHeight);
        overlay.setMaxSize(ASSET_WIDTH, imageHeight);

        if (slot.getColor() != null) {
            TotemNode totem = TOTEM_BOUNDS.createTotemNode(slot.getColor(), ASSET_WIDTH, imageHeight);
            totem.setHighlighted(highlightPredicate.test(slot.getColor()));
            overlay.getChildren().add(totem);
        }

        return new StackPane(imageView, overlay);
    }

    private Node createFallback(int index, OfferTrackElement slot, Predicate<Color> highlightPredicate) {
        VBox slotNode = new VBox(6);
        slotNode.setAlignment(Pos.CENTER);
        slotNode.setPadding(new Insets(8));
        slotNode.setPrefWidth(105);
        slotNode.getStyleClass().add("offer-track-fallback");

        Label indexLabel = new Label(String.valueOf(index + 1));
        indexLabel.getStyleClass().add("fallback-index");
        Label actions = new Label(formatActions(slot.getOfferActions()));
        actions.setWrapText(true);

        slotNode.getChildren().add(indexLabel);
        if (slot.getColor() == null) {
            Label emptySlot = new Label("Empty");
            emptySlot.getStyleClass().add("muted-text");
            slotNode.getChildren().add(emptySlot);
        } else {
            TotemNode totem = new TotemNode(slot.getColor(), 28.0);
            totem.setHighlighted(highlightPredicate.test(slot.getColor()));
            slotNode.getChildren().add(totem);
        }
        slotNode.getChildren().add(actions);
        return slotNode;
    }

    private static String formatActions(List<OfferAction> actions) {
        return actions.stream()
                .map(action -> switch (action) {
                    case TOP -> "Top";
                    case BOTTOM -> "Bottom";
                    case FOOD -> "+3 Food";
                })
                .collect(Collectors.joining(" / "));
    }
}
