package it.polimi.ingsw.am43.model.board;


import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.cards.TribeCard;

import java.util.*;

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
    private void shuffle(int seed) {  //to convert to long
        Random rSeed = new Random(seed);
        Collections.shuffle(this.deck,rSeed);
        this.deck.sort((a,b)-> Integer.compare(a.getEra(),b.getEra()));
    }

    public Map<Integer,Card> idTribeCardMap(){
        Map<Integer, Card> map = new HashMap<>();
        for(Card c : this.deck){
            map.put(c.getId,c);
        }
        return map;
    }
}
