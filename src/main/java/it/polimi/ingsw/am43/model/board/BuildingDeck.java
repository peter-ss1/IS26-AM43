package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BuildingDeck {

    private final Map<Integer, List<Building>> decksByEra;

    public BuildingDeck(int seed, Map<Integer, List<Building>> buildingDeck, List<Integer> numBuildings) {
        this.decksByEra = buildingDeck;
        this.shuffleAndSelect(seed, numBuildings);
    }

    public List<Building> revealEra(int era) {
        return this.decksByEra.remove(era);
    }

    private void shuffleAndSelect(int seed, List<Integer> numBuildings) {
        Random rSeed = new Random(seed);
        int i = 0;
        for (List<Building> eraList : decksByEra.values()) {
            Collections.shuffle(eraList, rSeed);
            eraList.subList(numBuildings.get(i++), eraList.size()).clear();
        }
    }

}