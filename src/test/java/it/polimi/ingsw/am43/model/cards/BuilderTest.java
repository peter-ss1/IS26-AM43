package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.CharacterType;
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
        Player player = new Player("alice");
        Builder builder = new Builder(1, 0, 2, 4);

        builder.tribeEntranceEffect(player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.BUILDER));
    }

    @Test
    void tribeEntranceEffectShouldIncreasePlayersBuildingDiscountByBuildersDiscount() {
        Player player = new Player("alice");
        Builder builder = new Builder(1, 0, 2, 4);

        builder.tribeEntranceEffect(player);

        assertEquals(2, player.getBuildingDiscount());
    }

    @Test
    void tribeEntranceEffectShouldAccumulateBuildingDiscountWhenMultipleBuildersEnter() {
        Player player = new Player("alice");
        Builder firstBuilder = new Builder(1, 0, 2, 1);
        Builder secondBuilder = new Builder(1, 0,3, 2);

        firstBuilder.tribeEntranceEffect(player);
        secondBuilder.tribeEntranceEffect(player);

        assertEquals(2, player.getTribe().getNumberByCharacterType(CharacterType.BUILDER));
        assertEquals(5, player.getBuildingDiscount());
    }
}
