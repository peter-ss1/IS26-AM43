package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.MockObserver;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuilderTest {
    @Test
    void constructorShouldStoreEraBuildingDiscountAndPrestigePoints() {
        Builder builder = new Builder(2, 0, 3, 5);

        assertEquals(2, builder.getEra());
        assertEquals(3, builder.getBuildingDiscount());
        assertEquals(5, builder.getPrestigePoints());
    }

    @Test
    void tribeEntranceEffectShouldAddBuilderToPlayersTribe() {
        Player player = new Player("alice", Color.CYAN);
        Builder builder = new Builder(1, 0, 2, 4);

        builder.tribeEntranceEffect(new MockObserver(), player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.BUILDER));
    }

    @Test
    void tribeEntranceEffectShouldIncreasePlayersBuildingDiscountByBuildersDiscount() {
        Player player = new Player("alice",Color.WHITE);
        Builder builder = new Builder(1, 0, 2, 4);

        builder.tribeEntranceEffect(new MockObserver(), player);

        assertEquals(2, player.getBuildingDiscount());
    }

    @Test
    void tribeEntranceEffectShouldAccumulateBuildingDiscountWhenMultipleBuildersEnter() {
        Player player = new Player("alice",Color.CYAN);
        Builder firstBuilder = new Builder(1, 0, 2, 1);
        Builder secondBuilder = new Builder(1, 0,3, 2);

        firstBuilder.tribeEntranceEffect(new MockObserver(), player);
        secondBuilder.tribeEntranceEffect(new MockObserver(), player);

        assertEquals(2, player.getTribe().getNumberByCharacterType(CharacterType.BUILDER));
        assertEquals(5, player.getBuildingDiscount());
    }
}
