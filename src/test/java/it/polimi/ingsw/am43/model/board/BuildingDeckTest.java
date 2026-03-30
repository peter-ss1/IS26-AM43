package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.FinalBuilding;
import it.polimi.ingsw.am43.model.cards.FinalEffect;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildingDeckTest {
    @Test
    void shouldInitialize() {
        List<Building> buildings1 = new ArrayList<>();
        buildings1.add(new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings1.add(new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings1.add(new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings1.add(new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings1.add(new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        List<Building> buildings2 = new ArrayList<>();
        buildings2.add(new FinalBuilding(2, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings2.add(new FinalBuilding(2, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings2.add(new FinalBuilding(2, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings2.add(new FinalBuilding(2, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings2.add(new FinalBuilding(2, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        List<Building> buildings3 = new ArrayList<>();
        buildings3.add(new FinalBuilding(3, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings3.add(new FinalBuilding(3, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings3.add(new FinalBuilding(3, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings3.add(new FinalBuilding(3, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings3.add(new FinalBuilding(3, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        Map<Integer, List<Building>> deckInit = new HashMap<>();
        deckInit.put(1, buildings1);
        deckInit.put(2, buildings2);
        deckInit.put(3, buildings3);
        List<Integer> buildingsInit = new ArrayList<>();
        buildingsInit.add(1);
        buildingsInit.add(2);
        buildingsInit.add(3);
        BuildingDeck deck = new BuildingDeck(1, deckInit, buildingsInit);
        assertEquals(1, buildings1.size());
        assertEquals(2, buildings2.size());
        assertEquals(3, buildings3.size());
        assertEquals(buildings1, deck.revealEra(1));
        assertEquals(buildings2, deck.revealEra(2));
        assertEquals(buildings3, deck.revealEra(3));
    }
}