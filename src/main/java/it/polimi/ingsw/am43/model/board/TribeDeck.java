package it.polimi.ingsw.am43.model.board;


import it.polimi.ingsw.am43.model.cards.Card;

import java.io.Serializable;
import java.util.List;

/**
 * The deck of tribe cards (characters and events) drawn during the game.
 * Cards are drawn from the top in a fixed, pre-shuffled order.
 */
public class TribeDeck implements Serializable {
    private final List<Card> deck;

    /**
     * @param deck the ordered list of tribe cards
     */
    public TribeDeck(List<Card> deck) {
        this.deck = deck;
    }

    /**
     * Draws and removes the top card of the deck.
     *
     * @return the drawn card
     */
    public Card draw() {
        return deck.removeFirst();
    }


    /** @return true if the deck has no cards left */
    public boolean isEmpty() {
        return this.deck.isEmpty();
    }
}
