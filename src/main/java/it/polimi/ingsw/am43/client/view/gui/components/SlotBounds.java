package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.model.enums.Color;
import javafx.scene.layout.AnchorPane;

/**
 * Normalized bounds used to position a totem inside an image-backed slot container.
 *
 * @param x the horizontal center position as a fraction of the container width
 * @param y the vertical center position as a fraction of the container height
 * @param width the totem width as a fraction of the container width
 * @param height the totem height as a fraction of the container height
 */
record SlotBounds(double x, double y, double width, double height) {
    TotemNode createTotemNode(Color color, double containerWidth, double containerHeight) {
        double nodeWidth = this.width * containerWidth;
        double nodeHeight = this.height * containerHeight;
        TotemNode totem = new TotemNode(color, nodeWidth, nodeHeight);
        AnchorPane.setLeftAnchor(totem, this.x * containerWidth - nodeWidth / 2.0);
        AnchorPane.setTopAnchor(totem, this.y * containerHeight - nodeHeight / 2.0);
        return totem;
    }
}
