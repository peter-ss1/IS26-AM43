package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.model.enums.Color;
import javafx.scene.layout.AnchorPane;

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
