package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RitualEventTest {
    private RitualEvent example;
    @Test
    void shouldAffectPlayers() {
        example = new RitualEvent(1, 0, -3);
        List<Player> players = new ArrayList<>();
        Player p1 = new Player("p1", Color.WHITE);
        Player p2 = new Player("p2",Color.BLACK);
        Player p3 = new Player("p3",Color.YELLOW);
        players.add(p1);
        players.add(p2);
        players.add(p3);

        p1.alterShamanStars(3);
        p3.alterShamanStars(4);

        example.affectPlayers(players);
        assertEquals(0, p1.getPrestigePoints());
        assertEquals(-3, p2.getPrestigePoints());
        assertEquals(5, p3.getPrestigePoints());
    }

    @Test
    void shouldTriggerBuilding() {
        example = new RitualEvent(1, 0, -3);
        List<Player> players = new ArrayList<>();
        Player p1 = new Player("p1",Color.BLACK);
        Player p2 = new Player("p2",Color.YELLOW);
        players.add(p1);
        players.add(p2);


        p1.getTribe().addCardToTribe(new RitualEventBuilding(1, 0, 0, 0, new EventEffect.NoLossInRitualEvent()));
        p1.getTribe().addCardToTribe(new RitualEventBuilding(1, 0, 0, 0, new EventEffect.DoubleWinInRitualEvent()));
        example.triggerBuilding(new SustenanceEventBuilding(1, 0, 0, 0, (player, event) -> {player.alterFood(2);}), p1);
        example.affectPlayers(players);
        assertEquals(10, p1.getPrestigePoints());
        assertEquals(2, p2.getPrestigePoints());

    }
}