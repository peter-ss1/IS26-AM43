package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PaintingEventTest {
    PaintingEvent example = new PaintingEvent(2, 0);
    @Test
    void shouldNotBePickable() {
        assertFalse(example.isPickable());
    }

    @Test
    void shouldAddToRow() {
        Row row = new Row();
        example.addToRow(row);

        assertEquals(1, row.size());
    }

    @Test
    void shouldAddToTop() {
        assertEquals(OfferAction.TOP, example.firstRowChoice());
    }

    @Test
    void shouldAffectPlayers() {
        List<Player> players = new ArrayList<>();
        Player p1 = new Player("p1", Color.WHITE);
        Player p2 = new Player("p2",Color.CYAN);
        players.add(p1);
        players.add(p2);

        p1.getTribe().addCardToTribe(new Artist(1, 0));
        p1.getTribe().addCardToTribe(new Artist(1, 0));

        example.affectPlayers(players);
        assertEquals(4, p1.getPrestigePoints());
        assertEquals(-2, p2.getPrestigePoints());
    }

    @Test
    void shouldTriggerBuilding() {
        Player p1 = new Player("p1",Color.WHITE);
        p1.getTribe().addCardToTribe(new Artist(1, 0));
        example.triggerBuilding(new SustenanceEventBuilding(1, 0, 0, 0, (player, event) -> {player.alterFood(2);}), p1);
        example.triggerBuilding(new PaintingEventBuilding(1, 0, 0, 0, new EventEffect.BonusPaintingEvent()), p1);
        assertEquals(1, p1.getFood());
    }
}