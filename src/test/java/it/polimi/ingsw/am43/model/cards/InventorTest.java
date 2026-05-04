package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.MockObserver;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventorTest {
    @Test
    void constructorShouldStoreEraAndInventorSymbol() {
        Inventor inventor = new Inventor(2, 0,InventorSymbol.BOAT);

        assertEquals(2, inventor.getEra());
        assertEquals(InventorSymbol.BOAT, inventor.getSymbol());
    }

    @Test
    void tribeEntranceEffectShouldAddInventorToPlayersTribe() {
        Player player = new Player("alice", Color.YELLOW);
        Inventor inventor = new Inventor(1, 0,InventorSymbol.FLUTE);

        inventor.tribeEntranceEffect(new MockObserver(), player);

        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.INVENTOR));
    }

    @Test
    void tribeEntranceEffectShouldNotChangePlayerResourcesOrDiscounts() {
        Player player = new Player("alice",Color.YELLOW);
        Inventor inventor = new Inventor(1, 0, InventorSymbol.ROPE);

        inventor.tribeEntranceEffect(new MockObserver(), player);

        assertEquals(0, player.getFood());
        assertEquals(0, player.getBuildingDiscount());
        assertEquals(0, player.getSustenanceDiscount());
        assertEquals(0, player.getShamanStars());
        assertEquals(0, player.getPrestigePoints());
    }

}
