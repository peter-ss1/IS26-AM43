package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BuildingDeckTest {
    private BuildingDeck buildingDeck = new BuildingDeck(1, new HashMap<>());

    @Test
    void draw() {
        ArrayList<Building> deck = new ArrayList<>();
        deck.add(new FinalBuilding(1, 1, 1, 1, new Effects.FinalPrestigePointsByCharacterType(CharacterType.HUNTER, 1)));
        Map<Integer, ArrayList<Building>> bDeck= new HashMap<>();
        bDeck.put(1, deck);
        buildingDeck = new BuildingDeck(1, bDeck);
        buildingDeck.draw(1);
    }

    @Test
    void revealEra() {
        ArrayList<Building> deck = new ArrayList<>();
        deck.add(new FinalBuilding(1, 1, 1, 1, new Effects.FinalPrestigePointsByCharacterType(CharacterType.HUNTER, 1)));
        Map<Integer, ArrayList<Building>> bDeck= new HashMap<>();
        bDeck.put(1, deck);
        buildingDeck = new BuildingDeck(1, bDeck);
        buildingDeck.revealEra(1);
    }

    @Test
    void idBuildingCardMap() {
        buildingDeck.idBuildingCardMap();
    }
}