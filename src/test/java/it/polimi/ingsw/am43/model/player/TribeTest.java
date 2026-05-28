package it.polimi.ingsw.am43.model.player;

import it.polimi.ingsw.am43.model.MockObserver;
import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TribeTest {
    private final GameObserver observer = new MockObserver();

    //  FinalBuilding, TribeBuilding e TimedBuilding richiedono nel
    //  costruttore un'implementazione di FinalEffect, TribeBonus e TimedEffect.
    //  Senza fornirne una non puoi creare quegli oggetti.


    static class NoOpFinalEffect implements FinalEffect {
        @Override
        public void manifest(Player player) {}
    }

    static class NoOpTribeBonus implements TribeBonus {

        @Override
        public int calculateBonus(Player player) {
            return 0;
        }

        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {

        }
    }

    static class NoOpTimedEffect implements TimedEffect {
        @Override
        public void manifest(Player player, Game game, Board board) {}
    }

    static class CountingFinalEffect implements FinalEffect {
        int timesCalled = 0;
        @Override
        public void manifest(Player player) { timesCalled++; }
    }

    static class FoodGivingTribeBonus implements TribeBonus {

        @Override
        public int calculateBonus(Player player) {
            return 2;
        }

        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {
            player.alterFood(bonus);
        }
    }

    static class CountingEventBuilding extends EventBuilding {
        int timesCalled = 0;
        public CountingEventBuilding() { super(1, 0, 0, 0); }
        @Override
        public void reactToEvent(GameObserver observer, Player player, HuntEvent event) { timesCalled++; }
    }



    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Lorenzo", Color.WHITE);
    }





    @Test
    void testGetTribeNumberEmpty() {
        assertEquals(0, player.getTribe().getTribeNumber());
    }

    @Test
    void testGetTribeNumberAfterAdds() {
        new Hunter(1, 0, false).tribeEntranceEffect(observer, player);
        new Artist(1, 0).tribeEntranceEffect(observer, player);
        new Shaman(1, 0, 2).tribeEntranceEffect(observer, player);
        assertEquals(3, player.getTribe().getTribeNumber());
    }


    @Test
    void testGetNumberByCharacterTypeHunter() {
        new Hunter(1, 0, false).tribeEntranceEffect(observer, player);
        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
    }

    @Test
    void testGetNumberByCharacterTypeInventor() {
        new Inventor(1, 0, InventorSymbol.BOAT).tribeEntranceEffect(observer, player);
        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.INVENTOR));
    }

    @Test
    void testGetNumberByCharacterTypeBuilder() {
        new Builder(1, 0, 1, 0).tribeEntranceEffect(observer, player);
        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.BUILDER));
    }

    @Test
    void testGetNumberByCharacterTypeGatherer() {
        new Gatherer(1, 0).tribeEntranceEffect(observer, player);
        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.GATHERER));
    }

    @Test
    void testGetNumberByCharacterTypeArtist() {
        new Artist(1, 0).tribeEntranceEffect(observer, player);
        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.ARTIST));
    }

    @Test
    void testGetNumberByCharacterTypeShaman() {
        new Shaman(1, 0, 1).tribeEntranceEffect(observer, player);
        assertEquals(1, player.getTribe().getNumberByCharacterType(CharacterType.SHAMAN));
    }

    @Test
    void testGetNumberByCharacterTypeDoesNotMix() {
        new Hunter(1, 0, false).tribeEntranceEffect(observer, player);
        assertEquals(0, player.getTribe().getNumberByCharacterType(CharacterType.ARTIST));
    }


    @Test
    void testAddFinalBuilding() {
        new FinalBuilding(1, 0,0, 0, new NoOpFinalEffect()).tribeEntranceEffect(observer, player);
        assertDoesNotThrow(() -> player.getTribe().activateFinalBuildings(player));
    }

    @Test
    void testAddTribeBuilding() {
        new TribeBuilding(1, 0,0, 0, new NoOpTribeBonus()).tribeEntranceEffect(observer, player);
        assertDoesNotThrow(() -> player.getTribe().activateTribeBuildings(observer, player));
    }

    @Test
    void testAddEventBuilding() {
        new CountingEventBuilding().tribeEntranceEffect(observer, player);
        assertDoesNotThrow(() ->
                player.getTribe().activateEventBuildings(observer, new HuntEvent(1,0), player));
    }

    @Test
    void testAddTimedBuilding() {
        new TimedBuilding(1, 0,0, 0, new NoOpTimedEffect()).tribeEntranceEffect(observer, player);
        assertDoesNotThrow(() -> player.getTribe().activateTimedBuilding(null, player, null));
    }


    @Test
    void testActivateFinalBuildingsCallsEffect() {
        CountingFinalEffect effect = new CountingFinalEffect();
        new FinalBuilding(1, 0, 0, 0, effect).tribeEntranceEffect(observer, player);
        player.getTribe().activateFinalBuildings(player);
        assertEquals(1, effect.timesCalled);
    }

    @Test
    void testActivateTribeBuildingsCallsEffect() {
        new TribeBuilding(1, 0, 0, 0, new FoodGivingTribeBonus()).tribeEntranceEffect(observer, player);
        int foodBefore = player.getFood();
        player.getTribe().activateTribeBuildings(observer, player);
        // bonus è 2, lastGivenBonus era già 2 dopo tribeEntranceEffect → delta = 0
        assertEquals(foodBefore, player.getFood());
    }

    @Test
    void testActivateEventBuildingsCallsReactToEvent() {
        CountingEventBuilding eb = new CountingEventBuilding();
        eb.tribeEntranceEffect(observer, player);
        player.getTribe().activateEventBuildings(observer, new HuntEvent(1,0), player);
        assertEquals(1, eb.timesCalled);
    }



    @Test
    void testGetNumberOfSetsZeroWhenMissingType() {
        new Hunter(1, 0, false).tribeEntranceEffect(observer, player);
        new Inventor(1, 0, InventorSymbol.BOAT).tribeEntranceEffect(observer, player);
        assertEquals(0, player.getTribe().getNumberOfSets());
    }

    @Test
    void testGetNumberOfSetsOneCompleteSet() {
        new Hunter(1, 0, false).tribeEntranceEffect(observer, player);
        new Inventor(1, 0, InventorSymbol.BOAT).tribeEntranceEffect(observer, player);
        new Builder(1, 0,1, 0).tribeEntranceEffect(observer, player);
        new Gatherer(1,0).tribeEntranceEffect(observer, player);
        new Artist(1,0).tribeEntranceEffect(observer, player);
        new Shaman(1, 0,1).tribeEntranceEffect(observer, player);
        assertEquals(1, player.getTribe().getNumberOfSets());
    }



    @Test
    void testGetInventorSymbolPairsEmpty() {
        assertEquals(0, player.getTribe().getInventorSymbolPairs());
    }

    @Test
    void testGetInventorSymbolPairsNoPairs() {
        new Inventor(1, 0,InventorSymbol.BOAT).tribeEntranceEffect(observer, player);
        new Inventor(1,0, InventorSymbol.HOOK).tribeEntranceEffect(observer, player);
        assertEquals(0, player.getTribe().getInventorSymbolPairs());
    }

    @Test
    void testGetInventorSymbolPairsOnePair() {
        new Inventor(1, 0,InventorSymbol.BOAT).tribeEntranceEffect(observer, player);
        new Inventor(1, 0,InventorSymbol.BOAT).tribeEntranceEffect(observer, player);
        assertEquals(1, player.getTribe().getInventorSymbolPairs());
    }


    @Test
    void testGetDistinctInventorSymbolsEmpty() {
        assertEquals(0, player.getTribe().getDistinctInventorSymbols());
    }

    @Test
    void testGetDistinctInventorSymbols() {
        new Inventor(1,0, InventorSymbol.BOAT).tribeEntranceEffect(observer, player);
        new Inventor(1, 0,InventorSymbol.BOAT).tribeEntranceEffect(observer, player);
        new Inventor(1,0, InventorSymbol.HOOK).tribeEntranceEffect(observer, player);
        assertEquals(2, player.getTribe().getDistinctInventorSymbols());
    }


    @Test
    void testGathererIncreasesSustenanceDiscount() {
        new Gatherer(1,0).tribeEntranceEffect(observer, player);
        assertEquals(3, player.getSustenanceDiscount());
    }

    @Test
    void testBuilderIncreasesBuildingDiscount() {
        new Builder(1, 0,2, 0).tribeEntranceEffect(observer, player);
        assertEquals(2, player.getBuildingDiscount());
    }

    @Test
    void testShamanIncreasesShamanStars() {
        new Shaman(1, 0,3).tribeEntranceEffect(observer, player);
        assertEquals(3, player.getShamanStars());
    }

    @Test
    void testActiveHunterAddsFood() {
        new Hunter(1, 0,true).tribeEntranceEffect(observer, player);
        assertEquals(1, player.getFood());
    }

    @Test
    void testInactiveHunterDoesNotAddFood() {
        new Hunter(1, 0,false).tribeEntranceEffect(observer, player);
        assertEquals(0, player.getFood());
    }
    @Test
    void testGetBuildersTotalPrestigePointsEmpty() {
        assertEquals(0, player.getTribe().getBuildersTotalPrestigePoints());
    }

    @Test
    void testGetBuildersTotalPrestigePoints() {
        new Builder(1, 0,0, 3).tribeEntranceEffect(observer, player);
        new Builder(1, 0,0, 2).tribeEntranceEffect(observer, player);
        assertEquals(5, player.getTribe().getBuildersTotalPrestigePoints());
    }

    @Test
    void testGetBuildingsTotalPrestigePointsEmpty() {
        assertEquals(0, player.getTribe().getBuildingsTotalPrestigePoints());
    }

    @Test
    void testGetBuildingsTotalPrestigePoints() {
        new FinalBuilding(1, 0,0, 3, new NoOpFinalEffect()).tribeEntranceEffect(observer, player);
        new EventBuilding(1, 0,0, 2) {}.pick(new MockObserver(), player);
        new TribeBuilding(1, 0,0, 4, new NoOpTribeBonus()).tribeEntranceEffect(observer, player);
        new TimedBuilding(1, 0,0, 1, new NoOpTimedEffect()).tribeEntranceEffect(observer, player);
        assertEquals(10, player.getTribe().getBuildingsTotalPrestigePoints());
    }
}

