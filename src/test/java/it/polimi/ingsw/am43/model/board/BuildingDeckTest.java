package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.FinalBuilding;
import it.polimi.ingsw.am43.model.cards.FinalEffect;
import it.polimi.ingsw.am43.model.utils.GameLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildingDeckTest {
    @Test
    void shouldInitialize() throws IOException {
        BuildingDeck deck = new BuildingDeck(new GameLoader(0).loadBuildingDeck(3));
        assertEquals(2, deck.revealEra(1).size());
        assertEquals(2, deck.revealEra(2).size());
        assertEquals(4, deck.revealEra(3).size());
    }
}