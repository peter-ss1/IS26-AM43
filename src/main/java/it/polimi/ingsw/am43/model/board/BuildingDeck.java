package it.polimi.ingsw.am43.model.board;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
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
    public List<Building> revealEra(int era) throws IllegalArgumentException{
        try {
            return this.decksByEra.get(era);
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("era not supported");}

    }

    private void shuffle(int seed){
        // to complete
    }
}