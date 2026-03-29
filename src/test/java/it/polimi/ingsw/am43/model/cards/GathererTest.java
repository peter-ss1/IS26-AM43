package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GathererTest {
    @Test
    void tribeEntranceEffectShouldAddGathererToPlayersTribe() {
        Player player = new Player("alice", Color.WHITE);
        Gatherer gatherer = new Gatherer(1, 0);

        gatherer.tribeEntranceEffect(player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.GATHERER));
    }

    @Test
    void tribeEntranceEffectShouldIncreaseSustenanceDiscountByThree() {
        Player player = new Player("alice",Color.CYAN);
        Gatherer gatherer = new Gatherer(1,0);

        gatherer.tribeEntranceEffect(player);

        assertEquals(3, player.getSustenanceDiscount());
    }

    @Test
    void tribeEntranceEffectShouldAccumulateSustenanceDiscountWithMultipleGatherers() {
        Player player = new Player("alice",Color.WHITE);
        Gatherer firstGatherer = new Gatherer(1,0);
        Gatherer secondGatherer = new Gatherer(2,0);

        firstGatherer.tribeEntranceEffect(player);
        secondGatherer.tribeEntranceEffect(player);

        assertEquals(2, player.getTribe().getNumberByCharacterType(CharacterType.GATHERER));
        assertEquals(6, player.getSustenanceDiscount());
    }

}
