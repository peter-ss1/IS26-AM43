package it.polimi.ingsw.am43.model.board;

import java.util.*;

import it.polimi.ingsw.am43.model.cards.*;

public class BuildingDeck {

    private final Map<Integer, ArrayList<Building>> decksByEra;

    public BuildingDeck(int seed, Map<Integer, ArrayList<Building>> buildingDeck) {
        this.decksByEra = buildingDeck;
        this.shuffle(seed);
    }

    public ArrayList<Building> draw(int era){
        return this.decksByEra.remove(era);
    }
    // best if era is saved here or draw becomes drawAndSetEra
    public ArrayList<Building> revealEra(int era) throws IllegalArgumentException{
        try {
            return this.decksByEra.get(era);
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("era not supported");}

    }

    private void shuffle(int seed){ // to convert in long
        Random rSeed = new Random(seed);
        for (Integer i: this.decksByEra.keySet()) {
            Collections.shuffle(this.decksByEra.get(i),rSeed);
        }
    }

    public Map<Integer,Building> idBuildingCardMap(){
        Map<Integer,Building> map = new HashMap<>();
        for(Integer i : this.decksByEra.keySet()){
            for(Building b : this.decksByEra.get(i)){
                map.put(b.getId(),b);
            }
        }
        return map;
    }
}