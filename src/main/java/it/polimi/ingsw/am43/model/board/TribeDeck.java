package it.polimi.ingsw.am43.model.board;


import it.polimi.ingsw.am43.model.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TribeDeck {
    private final List<Card> deck;

    public TribeDeck(int seed, ArrayList<Card> deck){
        this.deck= deck;
        this.shuffle(seed);
    }
    public Card draw(){
        Card tb= deck.getLast();
        deck.removeLast();
        return tb;
    }
    private void shuffle(int seed){
        //to complete
    }
}
