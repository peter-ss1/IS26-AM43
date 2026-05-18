package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Artist;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.cards.Gatherer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TribeDeckTest {
    private TribeDeck deck;

    @Test
    void draw() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Artist(1, 1));
        cards.add(new Artist(2, 1));
        cards.add(new Gatherer(1, 1));
        cards.add(new Artist(2, 1));
        cards.add(new Artist(3, 1));
        deck = new TribeDeck(cards);
    }

    @Test
    void shouldBeEmpty() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Artist(1, 1));
        deck = new TribeDeck(cards);
        deck.draw();
        assertTrue(deck.isEmpty());
    }
}