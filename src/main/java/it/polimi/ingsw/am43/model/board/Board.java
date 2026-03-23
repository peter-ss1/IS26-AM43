package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Board{

    private OrderQueue turnOrder;
    private TribeDeck tribeDeck;
    private BuildingDeck buildingDeck;
    private Row topRow;
    private Row bottomRow;
    private int currEra;
    private ArrayList<OfferTrackCard> offerTrack;

    public void setPlayerOnTrack(Player player, int position) throws IllegalArgumentException {
        try {
            this.offerTrack.get(position).setPlayer(player);//controlll is free
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("Index out of bounds");}
    }

    public void initBoard(ArrayList<Player> players, int numPlayers, int seed, ArrayList<Integer> foodModifiers, ArrayList<Card> tribeDeck, Map<Integer, ArrayList<Building>> buildingDeck, ArrayList<OfferTrackCard> offerTrack){
        this.turnOrder= new OrderQueue(players,foodModifiers);
        this.tribeDeck= new TribeDeck(seed,tribeDeck);
        this.buildingDeck= new BuildingDeck(seed,buildingDeck);
        this.topRow= new Row();
        this.bottomRow= new Row();
        this.currEra=1;
        this.offerTrack= offerTrack;
        while (this.bottomRow.size()<=numPlayers){
            Card cardDrawn= this.tribeDeck.draw();
            switch (cardDrawn.firstRowAction()){
                case "TOP":
                    this.topRow.addCard(cardDrawn);
                case "BOTTOM":
                    this.bottomRow.addCard(cardDrawn);
            }
        }
        while (this.topRow.size()<numPlayers+4){
            this.topRow.addCard(this.tribeDeck.draw());
        }
        for(Building building : this.buildingDeck.draw(1)){
            this.topRow.addCard(building);
        }
    }

    public List<OfferAction> getOfferTrackCardActions(int position) throws IllegalArgumentException {
        try {
            return this.offerTrack.get(position).getActions();
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("Index out of bounds");}
    }

    public int getCurrEra() {
        return this.currEra;
    }

    public void increaseCurrEra() throws IllegalStateException{
        if(this.currEra++ >3) throw new IllegalStateException("era not supported");
    }

    public void replenishTopRow(int amount){
        for (int i=0;i<amount;i++){
            this.topRow.addCard(this.tribeDeck.draw());
        }
    }
    //per i building ?

    public void removeCardTopRow(Card c) throws IllegalArgumentException{
        this.topRow.removeNonBuildingCard(c);
    }

    public void removeCardBottomRow(Card c){
        this.bottomRow.removeNonBuildingCard(c);
    }

    public void removeBuildingTopRow(Building b){
        this.topRow.removeBuilding(b);
    }
    public void removeBuildingBottomRow(Building b){
        this.bottomRow.removeBuilding(b);
    }
}
