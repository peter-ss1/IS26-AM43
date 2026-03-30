package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinalBuildingTest {
    private FinalBuilding example = new FinalBuilding(1, 0, 1, 1, new FinalEffect.FinalBonusPrestigePoints());

    @Test
    void shouldAddToRow() {
        Row row  = new Row();
        example.addToRow(row);
        assertEquals(1, row.size());
        example.removeFromRow(row);
        assertEquals(0, row.size());
    }

    @Test
    void shouldGetId() {
        assertEquals(0, example.getId());
    }

    @Test
    void shouldApplyCorrectEffect() {
        Player player = new Player("1",Color.WHITE);
        example.finalBuildingEffect(player);
        assertEquals(25, player.getPrestigePoints());
        player = new Player("2",Color.CYAN);
        player.getTribe().addCardToTribe(new Builder(1, 0, 2, 3));
        example = new  FinalBuilding(1, 0, 1, 1, new FinalEffect.FinalDoubleBuilderPrestigePoints());
        example.finalBuildingEffect(player);
        assertEquals(3, player.getPrestigePoints());

        player = new Player("3",Color.BLACK);
        player.getTribe().addCardToTribe(new Hunter(1, 0, false));
        player.getTribe().addCardToTribe(new Hunter(1, 0, false));
        example = new  FinalBuilding(1, 0, 1, 1, new FinalEffect.FinalPrestigePointsByCharacterType(CharacterType.HUNTER, 3));
        example.finalBuildingEffect(player);
        assertEquals(6, player.getPrestigePoints());

        player = new Player("4", Color.YELLOW);
        player.getTribe().addCardToTribe(new Hunter(1, 0, false));
        player.getTribe().addCardToTribe(new Artist(1, 0));
        player.getTribe().addCardToTribe(new Gatherer(1, 0));
        player.getTribe().addCardToTribe(new Shaman(1, 0, 2));
        player.getTribe().addCardToTribe(new Builder(1, 0,1,1));
        player.getTribe().addCardToTribe(new Inventor(1, 0, InventorSymbol.BOAT));
        example = new  FinalBuilding(1, 0, 1, 1, new FinalEffect.FinalPrestigePointsPerSet());
        example.finalBuildingEffect(player);
        assertEquals(6, player.getPrestigePoints());
    }
}