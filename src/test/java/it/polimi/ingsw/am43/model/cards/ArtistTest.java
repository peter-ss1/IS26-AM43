package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.MockObserver;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArtistTest {
    @Test
    void tribeEntranceEffectShouldAddArtistToPlayersTribe() {
        Player player = new Player("alice", Color.BLACK);
        Artist artist = new Artist(1, 0);

        artist.tribeEntranceEffect(new MockObserver(), player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.ARTIST));
    }

    @Test
    void tribeEntranceEffectShouldNotChangeOtherPlayerAttributes() {
        Player player = new Player("Alice", Color.RED);
        Artist artist = new Artist(1, 0);

        artist.pick(new MockObserver(), player);

        assertEquals(0, player.getFood());
        assertEquals(0, player.getBuildingDiscount());
        assertEquals(0, player.getSustenanceDiscount());
        assertEquals(0, player.getShamanStars());
        assertEquals(0, player.getPrestigePoints());
    }

}
