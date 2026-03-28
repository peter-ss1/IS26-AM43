package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SustenanceEventTest {
    private SustenanceEvent example = new SustenanceEvent(2, 0);
    @Test
    void shouldAffectPlayers() {
        List<Player> players = new ArrayList<>();
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Player p3 = new Player("p3");
        Player p4 = new Player("p4");
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        p2.getTribe().addCardToTribe(new Artist(1, 0));
        p2.alterSustenanceDiscount(3);

        p3.getTribe().addCardToTribe(new Artist(2, 0));
        p3.getTribe().addCardToTribe(new Artist(3, 0));
        p3.getTribe().addCardToTribe(new Artist(3, 0));
        p3.alterFood(1);

        p4.getTribe().addCardToTribe(new Artist(1, 0));
        p4.alterFood(2);

        example.affectPlayers(players);
        assertEquals(0, p1.getPrestigePoints());
        assertEquals(0, p1.getFood());
        assertEquals(0, p2.getFood());
        assertEquals(0, p2.getPrestigePoints());
        assertEquals(0, p3.getFood());
        assertEquals(-4, p3.getPrestigePoints());
        assertEquals(1, p4.getFood());
        assertEquals(0, p4.getPrestigePoints());
   }
    @Test
    void shouldAddToRow() {
        Row row = new Row();
        example.addToRow(row);
        assertEquals(1, row.size());
    }

    @Test
    void shouldTriggerBuilding() {
        Player p1 = new Player("p1");
        example.triggerBuilding(new SustenanceEventBuilding(1, 0, 0, 0, (player, event) -> {player.alterFood(2);}), p1);
        example.triggerBuilding(new PaintingEventBuilding(1, 0, 0, 0, (player, event) -> {player.alterFood(2);}), p1);
        example.triggerBuilding(new RitualEventBuilding(1, 0, 0, 0, (player, event) -> {player.alterFood(2);}), p1);
        assertEquals(2, p1.getFood());

    }

}