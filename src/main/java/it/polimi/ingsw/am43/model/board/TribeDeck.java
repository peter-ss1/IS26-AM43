package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.TribeCard;

import java.util.ArrayList;
import java.util.List;

public class TribeDeck {
    private final List<TribeCard> deck;

    public TribeDeck(int seed){
        this.deck= new ArrayList<TribeCard>();
        // to complete
    }
    public TribeCard draw(){
        TribeCard tb= deck.getLast();
        deck.removeLast();
        return tb;
    }
}
