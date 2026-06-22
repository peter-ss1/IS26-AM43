package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.model.enums.Color;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * JavaFX node that renders a colored player totem.
 */
public class TotemNode extends StackPane {
    private static final double ACTIVE_SHADOW_RADIUS = 18.0;

    private final Color playerColor;
    private final Rectangle body;

    /**
     * Creates a square totem node for the specified player color.
     *
     * @param playerColor the color represented by the totem
     * @param size the width and height of the totem
     */
    public TotemNode(Color playerColor, double size) {
        this(playerColor, size, size);
    }

    /**
     * Creates a totem node for the specified player color and dimensions.
     *
     * @param playerColor the color represented by the totem
     * @param width the width of the totem
     * @param height the height of the totem
     */
    public TotemNode(Color playerColor, double width, double height) {
        this.playerColor = playerColor;
        this.body = new Rectangle(width, height);
        this.body.setArcWidth(Math.min(width, height) * 0.18);
        this.body.setArcHeight(Math.min(width, height) * 0.18);
        this.body.setFill(Paint.valueOf(toPantoneHex(playerColor)));
        this.body.setStroke(Paint.valueOf(strokeHex(playerColor)));
        this.body.setStrokeWidth(2.0);
        this.body.setStrokeType(StrokeType.INSIDE);

        this.setAlignment(Pos.CENTER);
        this.setMinSize(width, height);
        this.setPrefSize(width, height);
        this.setMaxSize(width, height);
        this.getChildren().add(this.body);
    }

    /**
     * Updates the visual highlighted state of this totem.
     *
     * @param highlighted {@code true} to highlight this totem, {@code false} otherwise
     */
    public void setHighlighted(boolean highlighted) {
        if (highlighted) {
            DropShadow glow = new DropShadow();
            glow.setRadius(ACTIVE_SHADOW_RADIUS);
            glow.setSpread(0.35);
            glow.setColor(javafx.scene.paint.Color.web(toPantoneHex(this.playerColor)));
            this.setEffect(glow);
            this.body.setStrokeWidth(3.0);
        } else {
            this.setEffect(null);
            this.body.setStrokeWidth(2.0);
        }
    }

    /**
     * Updates whether this totem is visually configured as draggable.
     *
     * @param draggable {@code true} to show this totem as draggable, {@code false} otherwise
     */
    public void setDraggable(boolean draggable) {
        this.setCursor(draggable ? Cursor.HAND : Cursor.DEFAULT);
        this.setMouseTransparent(!draggable);
    }

    /**
     * Returns the hexadecimal color associated with the specified player color.
     *
     * @param color the player color to convert
     * @return the hexadecimal color string
     */
    public static String toPantoneHex(Color color) {
        return switch (color) {
            case RED -> "#EF3340";
            case YELLOW -> "#FECB00";
            case CYAN -> "#009CBD";
            case BLACK -> "#41273B";
            case WHITE -> "#FFFFFF";
        };
    }

    private static String strokeHex(Color color) {
        return color == Color.WHITE ? "#8C8173" : "#201A17";
    }
}
