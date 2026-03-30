package it.polimi.ingsw.am43.model.board;

import java.util.*;

import it.polimi.ingsw.am43.model.cards.*;

public class BuildingDeck {

    private final Map<Integer, ArrayList<Building>> decksByEra;

    public BuildingDeck(int seed, Map<Integer, ArrayList<Building>> buildingDeck) {
        this.decksByEra = buildingDeck;
        this.shuffle(seed);
    }

    public ArrayList<Building> revealEra(int era){
        return this.decksByEra.remove(era);
    }


    private void shuffle(int seed){ // to convert in long
        Random rSeed = new Random(seed);
        for (Integer i: this.decksByEra.keySet()) {
            Collections.shuffle(this.decksByEra.get(i),rSeed);
        }
    }

}