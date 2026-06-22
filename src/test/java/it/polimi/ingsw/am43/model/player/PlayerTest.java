package it.polimi.ingsw.am43.model.player;

import it.polimi.ingsw.am43.model.MockObserver;
import it.polimi.ingsw.am43.model.cards.Artist;
import it.polimi.ingsw.am43.model.cards.Builder;
import it.polimi.ingsw.am43.model.cards.FinalBuilding;
import it.polimi.ingsw.am43.model.cards.Inventor;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PlayerTest {

    private Player player;
    private final GameObserver observer = new MockObserver();

    @BeforeEach
    void setUp() {
        player = new Player("Lorenzo", Color.WHITE);
    }

    @Test
    void testGetNickname() {
        assertEquals("Lorenzo", player.getNickname());
    }

    @Test
    void testSetAndGetColor() {
        player.setColor(Color.RED);
        assertEquals(Color.RED, player.getColor());
    }

    @Test
    void testInitialFoodIsZero() {
        assertEquals(0, player.getFood());
    }

    @Test
    void testAlterFoodPositive() {
        player.alterFood(5);
        assertEquals(5, player.getFood());
    }

    @Test
    void testAlterFoodNegative() {
        player.alterFood(5);
        player.alterFood(-3);
        assertEquals(2, player.getFood());
    }

    @Test
    void testInitialPrestigePointsIsZero() {
        assertEquals(0, player.getPrestigePoints());
    }

    @Test
    void testAlterPrestigePointsPositive() {
        player.alterPrestigePoints(10);
        assertEquals(10, player.getPrestigePoints());
    }

    @Test
    void testAlterPrestigePointsNegative() {
        player.alterPrestigePoints(10);
        player.alterPrestigePoints(-4);
        assertEquals(6, player.getPrestigePoints());
    }

    @Test
    void testInitialSustenanceDiscountIsZero() {
        assertEquals(0, player.getSustenanceDiscount());
    }

    @Test
    void testAlterSustenanceDiscount() {
        player.alterSustenanceDiscount(2);
        assertEquals(2, player.getSustenanceDiscount());
    }

    @Test
    void testInitialBuildingDiscountIsZero() {
        assertEquals(0, player.getBuildingDiscount());
    }

    @Test
    void testAlterBuildingDiscount() {
        player.alterBuildingDiscount(3);
        assertEquals(3, player.getBuildingDiscount());
    }

    @Test
    void testInitialShamanStarsIsZero() {
        assertEquals(0, player.getShamanStars());
    }

    @Test
    void testAlterShamanStars() {
        player.alterShamanStars(1);
        assertEquals(1, player.getShamanStars());
    }

    @Test
    void testTribeIsNeverNull() {
        assertNotNull(player.getTribe());
    }

    @Test
    void testCountFinalPointsWithoutCards() {
        player.countFinalPoints();
        assertEquals(0, player.getPrestigePoints());
    }

    @Test
    void testCountFinalPointsWithBuildersFinalPrestigeBonus() {
        new Builder(1, 0, 0, 3).tribeEntranceEffect(observer, player);
        new Builder(1, 0, 0, 2).tribeEntranceEffect(observer, player);
        player.countFinalPoints();
        assertEquals(5, player.getPrestigePoints());
    }

    @Test
    void testCountFinalPointsWithInventorsSymbolExponentialBonus() {
        new Inventor(1, 0, InventorSymbol.BOAT).tribeEntranceEffect(observer, player);
        new Inventor(1, 0, InventorSymbol.HOOK).tribeEntranceEffect(observer, player);
        player.countFinalPoints();
        assertEquals(4, player.getPrestigePoints());
    }

    @Test
    void testCountFinalPointsWithArtistsPairsBonus() {
        new Artist(1, 0).tribeEntranceEffect(observer, player);
        new Artist(1, 0).tribeEntranceEffect(observer, player);
        player.countFinalPoints();
        assertEquals(10, player.getPrestigePoints());
    }

    @Test
    void testCountFinalPointsWithOddArtistNumber() {
        new Artist(1, 0).tribeEntranceEffect(observer, player);
        new Artist(1, 0).tribeEntranceEffect(observer, player);
        new Artist(1, 0).tribeEntranceEffect(observer, player);
        player.countFinalPoints();
        assertEquals(10, player.getPrestigePoints());
    }

    @Test
    void testCountFinalPointsWithFinalBuildingBonus() {
        TribeTest.CountingFinalEffect effect = new TribeTest.CountingFinalEffect();
        new FinalBuilding(1, 0, 0, 5, effect).tribeEntranceEffect(observer, player);
        player.countFinalPoints();
        assertEquals(5, player.getPrestigePoints());
        assertEquals(1, effect.timesCalled);
    }
}