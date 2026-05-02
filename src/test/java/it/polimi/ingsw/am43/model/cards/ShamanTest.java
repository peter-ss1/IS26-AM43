package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.MockObserver;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShamanTest {
    @Test
    void constructorShouldStoreEraAndShamanStars() {
        Shaman shaman = new Shaman(2, 0, 3);

        assertEquals(2, shaman.getEra());
        assertEquals(3, shaman.getShamanStars());
    }

    @Test
    void tribeEntranceEffectShouldAddShamanToPlayersTribe() {
        Player player = new Player("alice", Color.RED);
        Shaman shaman = new Shaman(1, 0, 2);

        shaman.tribeEntranceEffect(new MockObserver(), player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.SHAMAN));
    }

    @Test
    void tribeEntranceEffectShouldIncreasePlayersShamanStarsByCardValue() {
        Player player = new Player("alice", Color.YELLOW);
        Shaman shaman = new Shaman(1, 0, 2);

        shaman.tribeEntranceEffect(new MockObserver(), player);

        assertEquals(2, player.getShamanStars());
    }

    @Test
    void tribeEntranceEffectShouldAccumulateShamanStarsWithMultipleShamans() {
        Player player = new Player("alice", Color.RED);
        Shaman firstShaman = new Shaman(1, 0, 1);
        Shaman secondShaman = new Shaman(1, 0,3);

        firstShaman.tribeEntranceEffect(new MockObserver(), player);
        secondShaman.tribeEntranceEffect(new MockObserver(), player);

        assertEquals(2, player.getTribe().getNumberByCharacterType(CharacterType.SHAMAN));
        assertEquals(4, player.getShamanStars());
    }
}
