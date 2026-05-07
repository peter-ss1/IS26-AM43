package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.MockObserver;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TribeBuildingTest {
    TribeBuilding example;
    @Test
    void shouldApplyCorrectEffect() {
        Player player = new Player("1", Color.YELLOW);
        player.getTribe().addCardToTribe(new Inventor(1, 0, InventorSymbol.BOAT));
        player.getTribe().addCardToTribe(new Inventor(1, 0, InventorSymbol.BOAT));
        example = new TribeBuilding(1, 0, 1, 1, new TribeBonus.FoodOnInventorSymbolPair());
        example.tribeBuildingEffect(new MockObserver(), player);
        assertEquals(3, player.getFood());

        player = new Player("2",Color.CYAN);
        player.getTribe().addCardToTribe(new Builder(1, 0, 2, 3));
        example = new TribeBuilding(1, 0, 1, 1, new TribeBonus.BonusShamanStars());
        example.tribeBuildingEffect(new MockObserver(), player);
        assertEquals(3, player.getShamanStars());

        player = new Player("3",Color.WHITE);
        player.getTribe().addCardToTribe(new Artist(1, 0));
        player.getTribe().addCardToTribe(new Artist(1, 0));
        example = new  TribeBuilding(1, 0, 1, 1, new TribeBonus.SustenanceDiscountByCharacterType(CharacterType.ARTIST));
        example.tribeEntranceEffect(new MockObserver(), player);
        Artist artist = new Artist(1, 0);
        artist.tribeEntranceEffect(new MockObserver(), player);
        assertEquals(3, player.getSustenanceDiscount());

        player = new Player("4",Color.BLACK);
        player.getTribe().addCardToTribe(new Hunter(1, 0, false));
        player.getTribe().addCardToTribe(new Artist(1, 0));
        player.getTribe().addCardToTribe(new Gatherer(1, 0));
        player.getTribe().addCardToTribe(new Shaman(1, 0, 2));
        player.getTribe().addCardToTribe(new Builder(1, 0,1,1));
        player.getTribe().addCardToTribe(new Inventor(1, 0, InventorSymbol.BOAT));
        example = new  TribeBuilding(1, 0, 1, 1, new TribeBonus.FoodOnSet());
        example.tribeBuildingEffect(new MockObserver(), player);
        assertEquals(5, player.getFood());
    }

    @Test
    void shouldNotBePickedWithoutFood() {
        example = new TribeBuilding(1, 0, 1, 1, new TribeBonus.FoodOnInventorSymbolPair());
        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> {
            example.pick(new MockObserver(), new Player("p1", Color.WHITE));
        });
        assertEquals("Cannot pick building with insufficient food", exception.getMessage());
    }
}