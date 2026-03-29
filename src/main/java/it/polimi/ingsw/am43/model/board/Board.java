package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.cards.TribeCard;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.*;

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
            this.offerTrack.get(position).setPlayer(player);//control is free
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("Index out of bounds");}
    }


    public Board(ArrayList<Player> players, int numPlayers, int seed, ArrayList<Integer> foodModifiers, ArrayList<Card> tribeDeck, Map<Integer, ArrayList<Building>> buildingDeck, ArrayList<OfferTrackCard> offerTrack) throws RuntimeException{
        this.turnOrder= new OrderQueue(players,foodModifiers);
        this.tribeDeck= new TribeDeck(seed,tribeDeck);
        this.buildingDeck= new BuildingDeck(seed,buildingDeck);
        this.topRow= new Row();
        this.bottomRow= new Row();
        this.currEra=1;
        this.offerTrack= offerTrack;
        while (this.bottomRow.size()<=numPlayers){
            Card cardDrawn= this.tribeDeck.draw();
            switch (cardDrawn.firstRowChoice()){
                case OfferAction.TOP:
                    cardDrawn.addToRow(this.topRow);
                    break;
                case OfferAction.BOTTOM:
                    cardDrawn.addToRow(this.bottomRow);
                    break;
                default:
                    throw new RuntimeException("error in draw");
            }
        }
        while (this.topRow.size()<numPlayers+4){
            this.tribeDeck.draw().addToRow(this.topRow);
        }
        for(Building building : this.buildingDeck.revealEra(1)){
            building.addToRow(this.bottomRow);
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
        this.bottomRow.removeBuildings();
        this.moveTopToBottomBuildings();
        this.topRow.addAllBuildings(this.buildingDeck.revealEra(this.currEra));
    }

    public void replenishTopRow(int amount){
        for (int i=0;i<amount;i++){
            Card card= this.tribeDeck.draw();
            card.addToRow(this.topRow);
            if(card.getEra()>this.currEra){
                this.increaseCurrEra();
            }
        }
    }
    public ArrayList<OfferAction>getAvailableActions(Player player){
        return player.getAvailableActionsActions();
    }

    public OfferAction getCardPosition(Card card){
        if(card.isContainedInRow(this.topRow))return OfferAction.TOP;
        if(card.isContainedInRow(this.bottomRow))return OfferAction.BOTTOM;
        throw new IllegalArgumentException("card not in board");
    }

    public void removeAvailableAction(Player player, OfferAction offerAction) throws IllegalArgumentException{
        player.removeAvailableAction(offerAction);
    }

    public void removeCard(Card card) throws IllegalArgumentException{
        if(card.isContainedInRow(this.topRow)){
            card.removeFromRow(this.topRow);
            return;
        }
        if(card.isContainedInRow(this.bottomRow)){
            card.removeFromRow(this.topRow);
            return;
        }
        throw new IllegalArgumentException("card not in board");
    }

    public Player getNextPlayerInOrderQueue(){
        return this.turnOrder.peek();
    }

    public Player popNextPlayerInOrderQueue(){
        return this.turnOrder.pop();
    }

    public Optional<OfferTrackCard> getNextOccupiedOfferTrackCard(){
        for(OfferTrackCard otd : this.offerTrack){
            if(otd.getPlayer().isPresent()) return Optional.of(otd);
        }
        return Optional.empty();
    }

    public void  activateEvents(ArrayList<Player> players){
        this.bottomRow.activateEvents(players);
    }

    public void moveTopToBottomTribe(){
        this.bottomRow.addAllCharacters(this.topRow.getAllCharacters());
        this.bottomRow.addAllEvents(this.topRow.getAllEvents());
        this.topRow.removeCharacters();
        this.bottomRow.removeEvents();
    }
    public void moveTopToBottomBuildings(){
        this.bottomRow.addAllBuildings(this.topRow.getAllBuildings());
        this.topRow.removeBuildings();
    }

    public void returnPlayerToOrderQueue(Player player){
        this.turnOrder.append(player);
    }



}
