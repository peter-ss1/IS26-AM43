package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Holds the building cards split by era. Each era's buildings are revealed
 * once and removed from the deck when their era begins.
 */
public class BuildingDeck implements Serializable {

    private final Map<Integer, List<Building>> decksByEra;

    /**
     * @param buildingDeck the buildings grouped by era (1, 2, 3)
     */
    public BuildingDeck( Map<Integer, List<Building>> buildingDeck) {
        this.decksByEra = buildingDeck;
    }

    /**
     * Reveals and removes the buildings of the given era from the deck.
     *
     * @param era the era to reveal
     * @return the list of buildings of that era
     */
    public List<Building> revealEra(int era) {
        return this.decksByEra.remove(era);
    }


}