package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HuntEventTest {
    private final HuntEvent example = new HuntEvent(2, 0);

    @Test
    void shouldAffectPlayers() {
        List<Player> players = new ArrayList<>();
        Player p1 = new Player("p1", Color.WHITE);
        Player p2 = new Player("p2",Color.CYAN);
        players.add(p1);
        players.add(p2);

        p1.getTribe().addCardToTribe(new Hunter(1, 0, false));
        p1.getTribe().addCardToTribe(new Hunter(1, 0, false));

        example.affectPlayers(players);
        assertEquals(2, p1.getFood());
        assertEquals(4, p1.getPrestigePoints());
        assertEquals(0, p2.getFood());
        assertEquals(0, p2.getPrestigePoints());
    }

    @Test
    void shouldTriggerBuilding() {
        Player p1 = new Player("p1",Color.WHITE);
        p1.getTribe().addCardToTribe(new Hunter(1, 0, false));
        example.triggerBuilding(new HuntEventBuilding(1, 0, 0, 0, new EventEffect.BonusHuntEvent()), p1);
        example.triggerBuilding(new PaintingEventBuilding(1, 0, 0, 0, new EventEffect.BonusPaintingEvent()), p1);
        assertEquals(1, p1.getFood());
        assertEquals(1, p1.getPrestigePoints());
    }

    @Test
    void shouldNotGetPicked() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            example.pick(new Player("p1",Color.CYAN));
        });
        assertEquals("Events can't be picked", exception.getMessage());
    }
}