package it.polimi.ingsw.am43.client.view.gui.components;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * JavaFX node that displays a card image or a textual placeholder for a card identifier.
 */
public class CardNode extends VBox {
    private static final double CARD_WIDTH = 84.0;
    private static final double CARD_HEIGHT = 120.0;
    private static final String CARD_IMAGE_PATH = "/it/polimi/ingsw/am43/images/cards/front/card_%03d.png";
    private static final ResourceImageCache<Integer> IMAGE_CACHE = new ResourceImageCache<>();

    private boolean cardDisabled;

    /**
     * Creates a card node for the specified card identifier.
     *
     * @param cardId the identifier of the card to display
     */
    public CardNode(int cardId) {
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("card-node");
        CardMetadataRegistry.CardMetadata metadata = CardMetadataRegistry.get(cardId);
        Tooltip tooltip = new Tooltip(metadata.tooltipText());
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(320.0);
        Tooltip.install(this, tooltip);
        Image image = IMAGE_CACHE.get(cardId, id -> String.format(CARD_IMAGE_PATH, id));
        if (image == null) {
            this.getChildren().add(this.createPlaceholder(metadata));
        } else {
            this.getChildren().add(this.createImageView(image));
        }
    }

    /**
     * Sets the action invoked when this card is selected.
     *
     * @param action the action to invoke, or {@code null} to perform no action
     */
    public void setOnCardSelected(Runnable action) {
        this.setOnMouseClicked(event -> {
            if (!this.cardDisabled && action != null) {
                action.run();
                event.consume();
            }
        });
    }

    /**
     * Updates whether this card is disabled for selection.
     *
     * @param disabled {@code true} to disable this card, {@code false} otherwise
     */
    public void setCardDisabled(boolean disabled) {
        this.cardDisabled = disabled;
        this.setDisable(disabled);
        this.setStyleClassActive("card-disabled", disabled);
        if (disabled) {
            this.setClickable(false);
        }
    }

    /**
     * Updates the visual clickable state of this card.
     *
     * @param clickable {@code true} to show the card as clickable, {@code false} otherwise
     */
    public void setClickable(boolean clickable) {
        this.setStyleClassActive("card-clickable", clickable);
        this.setCursor(clickable ? Cursor.HAND : Cursor.DEFAULT);
    }

    /**
     * Updates the visual highlighted state of this card.
     *
     * @param highlighted {@code true} to highlight this card, {@code false} otherwise
     */
    public void setHighlighted(boolean highlighted) {
        this.setStyleClassActive("card-highlighted", highlighted);
    }

    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(CARD_WIDTH);
        imageView.setFitHeight(CARD_HEIGHT);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private StackPane createPlaceholder(CardMetadataRegistry.CardMetadata metadata) {
        VBox text = new VBox(4.0);
        text.setAlignment(Pos.CENTER);
        Label name = new Label(metadata.displayName());
        name.setWrapText(true);
        name.getStyleClass().add("card-placeholder-title");
        Label detail = new Label(metadata.shortDescription());
        detail.setWrapText(true);
        detail.getStyleClass().add("card-placeholder-detail");
        text.getChildren().addAll(name, detail);

        StackPane placeholder = new StackPane(text);
        placeholder.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        placeholder.getStyleClass().add("card-placeholder");
        return placeholder;
    }

    private void setStyleClassActive(String styleClass, boolean active) {
        if (active && !this.getStyleClass().contains(styleClass)) {
            this.getStyleClass().add(styleClass);
        }
        if (!active) {
            this.getStyleClass().remove(styleClass);
        }
    }
}
