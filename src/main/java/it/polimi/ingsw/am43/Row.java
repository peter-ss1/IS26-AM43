package it.polimi.ingsw.am43.Board;

import java.util.List;
import java.util.ArrayList;
import it.polimi.ingsw.am43.Board.Cards.*;
import it.polimi.ingsw.am43.Board.Players.*;

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

    public void addCard(Card card) {
        this.nonBuildingCards.add(card);
    }

    public void addCard(Building building) {//to talk later
        this.buildings.add(building);
    }

    //then those to probablt separated to animate better
    public void pickCard(Player player, Card card) throws IllegalArgumentException{
        if (!this.nonBuildingCards.remove(card)) throw new IllegalArgumentException("card picked not in row");
        card.tribeEntranceEffect;   // to be atomized
    }
    public void pickCard(Player player, Building building) throws IllegalArgumentException{
        if(!this.buildings.remove(card)) throw new IllegalArgumentException("card picked not in row");
        player.getTribe().addBuilding(building);   // to be atomized
    }



    public void replenish(int amount) {
        //pass under and draw
    }

    public void removeNonBuildingCards() {
        this.nonBuildingCards.clear();
    }

    public void removeBuildings() {
        this.buildings.clear();
    }

    public void activateEvents(ArrayList<Player> p) {
        this.eventResolutionQueue.forEach(e->e.affectPlayers(p));
    }

    public  void finalMoment(){
        this.nonBuildingCards.forEach(c->c.rowAction(this));
    }
}