package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Artist;
import it.polimi.ingsw.am43.model.cards.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TribeDeckTest {
    private TribeDeck tribeDeck= new TribeDeck(1, new ArrayList<Card>());
    @Test
    void draw() {
        ArrayList<Card> deck = new ArrayList<>();
        deck.add(new Artist(1, 1));
        tribeDeck = new TribeDeck(1, deck);
        assertEquals(Artist.class, tribeDeck.draw().getClass());
    }

}