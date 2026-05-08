package it.polimi.ingsw.am43.model.board;


import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.cards.TribeCard;

import java.io.Serializable;
import java.util.*;

public class TribeDeck implements Serializable {
    private final List<Card> deck;

    public TribeDeck( List<Card> deck){
        this.deck= deck;
    }
    public Card draw(){
        return deck.removeFirst();
    }


    public boolean isEmpty() {
        return this.deck.isEmpty();
    }
}
