package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.utils.GameLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildingDeckTest {
    @Test
    void shouldInitializeWithCorrectBuildingAmountsPerEra() throws IOException {
        GameLoader loader = new GameLoader(0);
        BuildingDeck deck = new BuildingDeck(loader.loadBuildingDeck(2));
        assertEquals(1, deck.revealEra(1).size());
        assertEquals(2, deck.revealEra(2).size());
        assertEquals(3, deck.revealEra(3).size());
        deck = new BuildingDeck(loader.loadBuildingDeck(3));
        assertEquals(2, deck.revealEra(1).size());
        assertEquals(2, deck.revealEra(2).size());
        assertEquals(4, deck.revealEra(3).size());
        deck = new BuildingDeck(loader.loadBuildingDeck(4));
        assertEquals(2, deck.revealEra(1).size());
        assertEquals(3, deck.revealEra(2).size());
        assertEquals(4, deck.revealEra(3).size());
        deck = new BuildingDeck(loader.loadBuildingDeck(5));
        assertEquals(2, deck.revealEra(1).size());
        assertEquals(3, deck.revealEra(2).size());
        assertEquals(5, deck.revealEra(3).size());

    }
}