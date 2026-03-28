package it.polimi.ingsw.am43.model.board;

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
    private Map<Integer,Card> idToTribe;
    private Map<Integer,Building> idToBuilding;



    public Game(int np, String nk) throws IOException {
        this.availableColors = new ArrayList<>(Arrays.asList(Color.values()));
        this.numPlayers=np;
        this.players=new ArrayList<>();
        this.addPlayer(nk);
        this.phase= GamePhase.PREPARATION;
        this.board=new Board();
        this.idToTribe = new HashMap<>();
        this.idToBuilding = new HashMap<>();
    }


    public ArrayList<Color> getAvailableColors() {
        return new ArrayList<>(this.availableColors);
    }

    public void setIdTribeCard(){
        this.idToTribe=this.board.idTribeCardMap();
    }
    public void setIdBuildingCard(){
        this.idToBuilding= this.board.idBuildingCardMap();
    }

    public void pickColor(String name, Color color) throws IllegalArgumentException {
        if(!this.availableColors.remove(color)) throw new IllegalArgumentException("Chosen color is not available");
        if(this.players.stream().noneMatch(p->p.getNickname().equals(name))) throw new IllegalArgumentException("Player is not registered in the game");
        players.forEach(p->{
            if (p.getNickname().equals(name))  p.setColor(color) ;
        });
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public void addPlayer(String nickname) throws IllegalArgumentException, IOException {
        if(this.players.stream().anyMatch(p->p.getNickname().equals(nickname))) throw new IllegalArgumentException("nickname already in use");
        if(this.players.size()==numPlayers) throw new IllegalArgumentException("exiding player");
        this.players.add(new Player(nickname));
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

    public void placeTotemOnTrack(String name, int position) throws IllegalArgumentException{
        Player np= this.getPlayerByName(name);
        this.board.setPlayerOnTrack(np,position);
    }

    public Player getPlayerByName(String name) throws IllegalArgumentException{
        return players.stream().filter(p->p.getNickname().equals(name)).findFirst().orElseThrow(()->new IllegalArgumentException("PLayer not registered"));
    }
    public Card getCardById (int id){
        return this.idToTribe.get(id);
    }
    public Building getBuildingById(int id){
        return this.idToBuilding.get(id);
    }

    public void pickCard(Card card, Player player)throws IllegalArgumentException{
        if(!this.currPlayer.equals(player) || !this.players.contains(player)) throw new IllegalArgumentException(("wrong player"));
        if(player.getAvailableActionsActions()==null) throw  new IllegalArgumentException("player has no actions left");
        if(!player.getAvailableActionsActions().contains(this.board.getCardPosition(card))) throw new IllegalArgumentException("player cannot pick that card");
        card.pick(player);
        player.removeAvailableAction(this.board.getCardPosition(card));
        this.board.removeCard(card);
    }


    //ask
    public void endCurrentTurn(Player player){

    }
    public void resolveOffer(){

    }



}
