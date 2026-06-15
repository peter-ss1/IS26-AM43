package it.polimi.ingsw.am43.client.view.gui.components;

import it.polimi.ingsw.am43.client.ClientPlayer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaFX node that displays the cards in a player's tribe grouped by metadata type.
 */
public class PlayerTribeNode extends VBox {
    /**
     * Creates a node for the specified player's tribe.
     *
     * @param player the player whose tribe is displayed
     * @param ownTribe {@code true} if the displayed tribe belongs to the local player, {@code false} otherwise
     */
    public PlayerTribeNode(ClientPlayer player, boolean ownTribe) {
        this.setSpacing(8.0);
        this.getStyleClass().add("tribe-view");
        if (ownTribe) {
            this.getStyleClass().add("own-tribe");
        }
        this.render(player, ownTribe);
    }

    private void render(ClientPlayer player, boolean ownTribe) {
        Label title = new Label(this.createTitle(player, ownTribe));
        title.getStyleClass().add("tribe-title");
        this.getChildren().add(title);

        Map<String, List<Integer>> groupedCards = this.groupTribeCards(player.getTribe());
        if (groupedCards.isEmpty()) {
            Label emptyLabel = new Label("This tribe is still empty.");
            emptyLabel.getStyleClass().add("muted-text");
            this.getChildren().add(emptyLabel);
            return;
        }

        HBox columns = new HBox(12.0);
        columns.setAlignment(Pos.TOP_LEFT);
        columns.getStyleClass().add("tribe-columns");

        groupedCards.forEach((groupName, cardIds) -> {
            VBox group = new VBox(6.0);
            group.setAlignment(Pos.TOP_CENTER);
            group.getStyleClass().add("tribe-column");
            Label groupLabel = new Label(groupName);
            groupLabel.getStyleClass().add("tribe-group-title");

            VBox cardsPane = new VBox(6.0);
            cardsPane.setAlignment(Pos.TOP_CENTER);
            for (int cardId : cardIds) {
                cardsPane.getChildren().add(new CardNode(cardId));
            }

            group.getChildren().addAll(groupLabel, cardsPane);
            columns.getChildren().add(group);
        });
        this.getChildren().add(columns);
    }

    private String createTitle(ClientPlayer player, boolean ownTribe) {
        if (ownTribe) {
            return "Your tribe";
        }
        return player.getNickname() + "'s tribe";
    }

    private Map<String, List<Integer>> groupTribeCards(List<Integer> cardIds) {
        if (cardIds.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return CardMetadataRegistry.groupByType(cardIds);
    }
}
