package it.polimi.ingsw.am43.model.board;

import java.util.List;
import java.util.ArrayList;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.player.*;

public class Row {

    private final List<Card> nonBuildingCards;
    private final List<Building> buildings;
    private List<Event> eventResolutionQueue;

    public Row() {
        this.nonBuildingCards = new ArrayList<>();
        this.buildings = new ArrayList<>();
    }

    public List<Card> getNonBuildingCards() {
        return new ArrayList<>(this.nonBuildingCards);
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(this.buildings);
    }

    public int size(){
        return this.buildings.size() + this.nonBuildingCards.size();
    }


    public void addCard(Card card) {
        this.nonBuildingCards.add(card);
    }

    public void addCard(Building building) {//to talk later
        this.buildings.add(building);
    }

    public void removeNonBuildingCards() {
        this.nonBuildingCards.clear();
    }

    public void removeBuildings() {
        this.buildings.clear();
    }

    public void removeNonBuildingCard(Card c)throws IllegalArgumentException{
        if(!this.nonBuildingCards.remove(c)) throw new IllegalArgumentException(("card not in row"));
    }
    public void removeBuilding(Building b)throws IllegalArgumentException{
        if(!this.buildings.remove(b)) throw new IllegalArgumentException(("card not in row"));
    }

    public void activateEvents(ArrayList<Player> p) {
        this.eventResolutionQueue.forEach(e->e.affectPlayers(p));
    }

    public  void finalMoment(){
        this.nonBuildingCards.forEach(c->c.addToRow(this));
    }

    public void roundEndingRow(){
        //ask what it does
    }
}