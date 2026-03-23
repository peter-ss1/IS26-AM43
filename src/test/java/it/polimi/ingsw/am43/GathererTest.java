package it.polimi.ingsw.am43;

import it.polimi.ingsw.am43.model.cards.Gatherer;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GathererTest {
    @Test
    void tribeEntranceEffectShouldAddGathererToPlayersTribe() {
        Player player = new Player("alice");
        Gatherer gatherer = new Gatherer(1);

        gatherer.tribeEntranceEffect(player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.GATHERER));
    }

    @Test
    void tribeEntranceEffectShouldIncreaseSustenanceDiscountByThree() {
        Player player = new Player("alice");
        Gatherer gatherer = new Gatherer(1);

        gatherer.tribeEntranceEffect(player);

        assertEquals(3, player.getSustenanceDiscount());
    }

    @Test
    void tribeEntranceEffectShouldAccumulateSustenanceDiscountWithMultipleGatherers() {
        Player player = new Player("alice");
        Gatherer firstGatherer = new Gatherer(1);
        Gatherer secondGatherer = new Gatherer(2);

        firstGatherer.tribeEntranceEffect(player);
        secondGatherer.tribeEntranceEffect(player);

        assertEquals(2, player.getTribe().getNumberByCharacterType(CharacterType.GATHERER));
        assertEquals(6, player.getSustenanceDiscount());
    }

}
