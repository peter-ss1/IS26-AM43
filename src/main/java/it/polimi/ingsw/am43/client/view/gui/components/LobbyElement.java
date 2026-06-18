package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.client.LobbyInfo;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class LobbyElement extends ListCell<LobbyInfo> {
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