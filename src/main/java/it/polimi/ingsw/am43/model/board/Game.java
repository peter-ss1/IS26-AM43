package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.player.Player;

import java.io.IOException;
import java.util.*;


import it.polimi.ingsw.am43.model.enums.*;
import it.polimi.ingsw.am43.model.player.*;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.utils.GameLoader;

public class Game {

    private List<Color> availableColors;
    private List<Player> players;
    private int numPlayers;
    private Player currPlayer;
    private GamePhase phase;
    private Board board;
    public Map<Integer,Card> idToCard;



    public Game(int np, String nk, Color color) {
        this.availableColors = new ArrayList<>(Arrays.asList(Color.values()));
        this.numPlayers=np;
        this.players=new ArrayList<>();
        this.addPlayer(nk, color);
        this.phase= GamePhase.PREPARATION;
        this.idToCard = new HashMap<>();
    }

    public ArrayList<Color> getAvailableColors() {
        return new ArrayList<>(this.availableColors);
    }

    public void initBoard(ArrayList<Player> players, int numPlayers, int seed, ArrayList<Integer> foodModifiers, ArrayList<Card> tribeDeck, Map<Integer, ArrayList<Building>> buildingDeck, ArrayList<OfferTrackCard> offerTrack) throws RuntimeException{
        this.board=new Board(players,numPlayers,seed,foodModifiers,tribeDeck,buildingDeck,offerTrack);
        this.currPlayer=this.board.getNextPlayerInOrderQueue();
    }

    public void setIdCard(Map<Integer,Card> map){
        this.idToCard.putAll(map);
    }


    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public void addPlayer(String nickname, Color color) throws IllegalArgumentException, RuntimeException {
        if(this.players.stream().anyMatch(p->p.getNickname().equals(nickname))) throw new IllegalArgumentException("nickname already in use");
        if(this.players.size()==numPlayers) throw new IllegalArgumentException("exiding player");
        if(!this.availableColors.remove(color)) throw new IllegalArgumentException("Chosen color is not available");
        this.players.add(new Player(nickname,color));
        if(this.players.size()==numPlayers){
            this.getPhase().resolvePhase(this,this.board);

        }
    }
    public int getNumPlayers() {
        return this.numPlayers;
    }
    public Player getCurrPlayer() {
        return this.currPlayer;
    }
    public void setCurrPlayer(Player player) throws IllegalArgumentException{
        if(!this.players.contains(player)) throw new IllegalArgumentException(("Player is not registered in the game"));
        this.currPlayer=player;
    }//to remove

    public GamePhase getPhase() {
        return this.phase;
    }
    public void setPhase(GamePhase phase) {
        this.phase=phase;
    }

    public void placeTotemOnTrack(Player player, int position) throws IllegalArgumentException{
        if(!player.equals(this.currPlayer)) throw new IllegalArgumentException("player should not move");
        this.board.setPlayerOnTrack(player,position);
        this.board.popNextPlayerInOrderQueue();
        this.currPlayer=this.board.getNextPlayerInOrderQueue();
        if(this.board.isOrderQueueEmpty())
            this.getPhase().resolvePhase(this,this.board);
    }

    public Player getPlayerByName(String name) throws IllegalArgumentException{
        return players.stream().filter(p->p.getNickname().equals(name)).findFirst().orElseThrow(()->new IllegalArgumentException("PLayer not registered"));
    }
    public Card getCardById (int id){
        return this.idToCard.get(id);
    }

    public void pickCard(Card card, Player player)throws IllegalArgumentException{
        if(!this.currPlayer.equals(player) || !this.players.contains(player)) throw new IllegalArgumentException(("wrong player"));
        if(player.getAvailableActionsActions()==null) throw  new IllegalArgumentException("player has no actions left");
        if(!player.getAvailableActionsActions().contains(this.board.getCardPosition(card))) throw new IllegalArgumentException("player cannot pick that card");
        if(!this.board.containsCard(card)) throw new IllegalArgumentException("card not in game");
        card.pick(player);
        player.removeAvailableAction(this.board.getCardPosition(card));
        this.board.removeCard(card);
    }


    public void endCurrentTurn(Player player){
        if (player != currPlayer) throw new IllegalArgumentException("not active player");
        if (this.phase != GamePhase.ACTION_RESOLUTION && this.phase != GamePhase.DRAW_FROM_TOP_BONUS_ACTION) throw new IllegalStateException("wrong phase");
        if (board.checkEndTurnCondition(player)) {
            board.returnPlayerToOrderQueue(player);
            board.getNextOccupiedOfferTrackCard()
                    .ifPresentOrElse(offer -> {
                        setCurrPlayer(offer.getPlayer().orElseThrow(()->new IllegalArgumentException("Player is not registered")));
                        resolveOffer(this.getCurrPlayer());
                    }, () -> {
                        this.getPhase().resolvePhase(this, this.board);
                    });
        }
        else throw new IllegalArgumentException("Available actions remaining");
    }

    public void resolveOffer(Player player) {
        if (player.getAvailableActionsActions().isEmpty() || board.isOfferResolved(player)) {
            board.returnPlayerToOrderQueue(player);
            board.getNextOccupiedOfferTrackCard()
                    .ifPresentOrElse(offer -> {
                        setCurrPlayer(offer.getPlayer().orElseThrow(()->new IllegalArgumentException("Player is not registered")));
                        resolveOffer(this.getCurrPlayer());
                    }, () -> {
                        this.getPhase().resolvePhase(this, this.board);
                    });
        }

    }


}
