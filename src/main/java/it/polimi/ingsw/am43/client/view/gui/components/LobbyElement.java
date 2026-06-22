package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.client.LobbyInfo;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
/**
 * Custom {@link ListCell} for displaying {@link LobbyInfo} in a JavaFX ListView.
 */
public class LobbyElement extends ListCell<LobbyInfo> {

    /**
     * Updates the cell's visual representation.
     * Clears the cell if it is empty or the lobby is null. Otherwise, constructs
     * an {@link HBox} containing the lobby ID and player count.
     *
     *
     * @param lobby The lobby data to display.
     * @param empty True if the cell represents an empty row.
     */
    @Override
    protected void updateItem(LobbyInfo lobby, boolean empty) {
        super.updateItem(lobby, empty);

        if (empty || lobby == null) {
            setText(null);
            setGraphic(null);
        } else {
            HBox container = new HBox(10);
            Label nameLabel = new Label("Lobby #" + lobby.getLobbyId());
            Label countLabel = new Label(lobby.getCurrentPlayers() + "/" + lobby.getNumPlayers());
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            container.getChildren().addAll(nameLabel, spacer, countLabel);
            container.getStyleClass().add("lobby-info");
            setGraphic(container);
        }
    }
}