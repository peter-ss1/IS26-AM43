package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BuildingDeck {

    private final Map<Integer, List<Building>> decksByEra;

    public BuildingDeck( Map<Integer, List<Building>> buildingDeck) {
        this.decksByEra = buildingDeck;
    }

    public List<Building> revealEra(int era) {
        return this.decksByEra.remove(era);
    }


}