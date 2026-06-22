package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardTest {
    @Test
    void shouldIncreaseCurrentEraDuringRowReplenishment() {
        List<Building> buildings1 = new ArrayList<>();
        buildings1.add(new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        List<Building> buildings2 = new ArrayList<>();
        buildings2.add(new FinalBuilding(2, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings2.add(new FinalBuilding(2, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        List<Building> buildings3 = new ArrayList<>();
        buildings3.add(new FinalBuilding(3, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings3.add(new FinalBuilding(3, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        buildings3.add(new FinalBuilding(3, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints()));
        Map<Integer, List<Building>> deckInit = new HashMap<>();
        deckInit.put(1, buildings1);
        deckInit.put(2, buildings2);
        deckInit.put(3, buildings3);
        List<Card> cards = new ArrayList<>();
        cards.add(new Artist(1, 1));
        cards.add(new Artist(2, 1));
        cards.add(new Gatherer(2, 1));
        cards.add(new Artist(2, 1));
        cards.add(new Artist(2, 1));
        Board board = new Board(new ArrayList<>(), -4,  new ArrayList<>(), cards, deckInit, new ArrayList<>(), 0);
        board.replenishTopRow(3);
        assertEquals(2, board.getCurrEra());
    }
}