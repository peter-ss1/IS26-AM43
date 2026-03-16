package it.polimi.ingsw.am43.model.board;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import it.polimi.ingsw.am43.model.cards.*;

public class BuildingDeck {

    private final Map<Integer, List<Building>> decksByEra;

    public BuildingDeck(int seed) {
        this.decksByEra = new HashMap<>();
    }

    public Building draw(){
        int i=0;
        while(decksByEra.get(i).isEmpty() && i<4)i++;
        if(i>3) return null;
        else {
            Building b= decksByEra.get(i).getLast();
            decksByEra.get(i).removeLast();
            return b;
        }
    }
    // best if era is saved here or draw becomes drawAndSetEra
    public void revealEra(int era) {

    }
}