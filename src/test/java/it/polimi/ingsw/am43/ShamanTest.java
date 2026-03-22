package it.polimi.ingsw.am43;

import it.polimi.ingsw.am43.model.cards.Shaman;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShamanTest {
    @Test
    void constructorShouldStoreEraAndShamanStars() {
        Shaman shaman = new Shaman(2, 3);

        assertEquals(2, shaman.getEra());
        assertEquals(3, shaman.getShamanStars());
    }

    @Test
    void tribeEntranceEffectShouldAddShamanToPlayersTribe() {
        Player player = new Player("alice");
        Shaman shaman = new Shaman(1, 2);

        shaman.tribeEntranceEffect(player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.SHAMAN));
    }

    @Test
    void tribeEntranceEffectShouldIncreasePlayersShamanStarsByCardValue() {
        Player player = new Player("alice");
        Shaman shaman = new Shaman(1, 2);

        shaman.tribeEntranceEffect(player);

        assertEquals(2, player.getShamanStars());
    }

    @Test
    void tribeEntranceEffectShouldAccumulateShamanStarsWithMultipleShamans() {
        Player player = new Player("alice");
        Shaman firstShaman = new Shaman(1, 1);
        Shaman secondShaman = new Shaman(1, 3);

        firstShaman.tribeEntranceEffect(player);
        secondShaman.tribeEntranceEffect(player);

        assertEquals(2, player.getTribe().getNumberByCharacterType(CharacterType.SHAMAN));
        assertEquals(4, player.getShamanStars());
    }
}
