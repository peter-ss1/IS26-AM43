package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HunterTest {
    @Test
    void constructorShouldStoreActiveFlag() {
        Hunter activeHunter = new Hunter(1, 0,true);
        Hunter inactiveHunter = new Hunter(1, 0,false);

        assertTrue(activeHunter.isActive());
        assertFalse(inactiveHunter.isActive());
    }

    @Test
    void tribeEntranceEffectShouldAddHunterToPlayersTribeEvenWhenInactive() {
        Player player = new Player("alice", Color.BLACK);
        Hunter hunter = new Hunter(1, 0,false);

        hunter.tribeEntranceEffect(player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
    }

    @Test
    void tribeEntranceEffectShouldNotIncreaseFoodWhenHunterIsInactive() {
        Player player = new Player("alice", Color.BLACK);
        Hunter hunter = new Hunter(1, 0,false);

        hunter.tribeEntranceEffect(player);

        assertEquals(0, player.getFood());
    }

    @Test
    void tribeEntranceEffectShouldIncreaseFoodByCurrentNumberOfHuntersWhenHunterIsActive() {
        Player player = new Player("alice", Color.BLACK);
        Hunter hunter = new Hunter(1, 0,true);

        hunter.tribeEntranceEffect(player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
        assertEquals(1, player.getFood());
    }

    @Test
    void tribeEntranceEffectShouldCountPreviouslyPresentHuntersWhenActiveHunterEnters() {
        Player player = new Player("alice", Color.BLACK);
        Hunter firstHunter = new Hunter(1, 0,false);
        Hunter secondHunter = new Hunter(1, 0,true);

        firstHunter.tribeEntranceEffect(player);
        secondHunter.tribeEntranceEffect(player);

        assertEquals(2, player.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
        assertEquals(2, player.getFood());
    }

    @Test
    void multipleActiveHuntersShouldAccumulateFoodAccordingToUpdatedHunterCount() {
        Player player = new Player("alice", Color.BLACK);
        Hunter firstHunter = new Hunter(1, 0,true);
        Hunter secondHunter = new Hunter(1, 0,true);

        firstHunter.tribeEntranceEffect(player);   // +1
        secondHunter.tribeEntranceEffect(player);  // +2

        assertEquals(2, player.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
        assertEquals(3, player.getFood());
    }

}
