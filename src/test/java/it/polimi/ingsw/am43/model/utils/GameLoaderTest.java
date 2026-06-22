package it.polimi.ingsw.am43.model.utils;

import it.polimi.ingsw.am43.model.cards.Building;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameLoaderTest {
    private final GameLoader gameLoader = new GameLoader(123L);

    @Test
    void gameShouldLoadWithCorrectlySizedCollections() throws IOException {
        for (int i = 2; i <= 5; i++) {
            assertEquals(i, gameLoader.loadFoodModifiers(i).size());
            assertEquals(i + 2, gameLoader.loadOfferTrackCard(i).size());
            assertEquals(10 * (i + 4) + i + 1, gameLoader.loadTribeDeck(i).size());
            Map<Integer, List<Building>> buildingDeck = gameLoader.loadBuildingDeck(i);
            assertTrue(buildingDeck.get(1).size() < buildingDeck.get(3).size());
        }
    }

}