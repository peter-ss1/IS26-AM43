package it.polimi.ingsw.am43.model.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import it.polimi.ingsw.am43.model.enums.*;
import it.polimi.ingsw.am43.model.player.*;
import it.polimi.ingsw.am43.model.cards.*;

public class Game {

    private  List<Color> availableColors;
    private List<Player> players;
    private int numPlayers;
    private Player currPlayer;
    private GamePhase phase;
    private OrderQueue turnOrder;
    private TribeDeck tribeDeck;
    private BuildingDeck buildingDeck;
    private Row topRow;
    private Row bottomRow;
    private int currEra;
    private ArrayList<OfferTrackCard> offerTrack;

    public Game(){
        //da parlarne per implementazione funzioni avanzate
    }


    public ArrayList<Color> getAvailableColors() {
        return new ArrayList<>(this.availableColors);
    }

    public void pickColor(Player player, Color color) throws IllegalArgumentException {
        if(!this.availableColors.remove(color)) throw new IllegalArgumentException("Chosen color is not available");
        if(!this.players.contains((player))) throw new IllegalArgumentException("Player is not registered in the game");
        player.setColor(color);
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public void addPlayer(String nickname) throws IllegalArgumentException {
        if(this.players.stream().anyMatch(p->p.getNickname().equals(nickname))) throw new IllegalArgumentException("nickname already in use");
        this.players.add(new Player(nickname));
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
    }

    public GamePhase getPhase() {
        return this.phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase=phase;
    }

    public char getOfferTrackCardLetter(int position) throws IllegalArgumentException {
        try {
            return this.offerTrack.get(position).getLetter();
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("Index out of bounds");}
    }

    public Optional<Player> getPlayerOnTrackCard(int position) throws IllegalArgumentException{
        try {
            return this.offerTrack.get(position).getPlayer();
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("Index out of bounds");}

    }

    public void setPlayerOnTrack(Player player, int position) throws IllegalArgumentException {
        if(!this.players.contains(player)) throw new IllegalArgumentException(("Player is not registered in the game"));
        try {
            this.offerTrack.get(position).setPlayer(player);
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("Index out of bounds");}
    }

    public int getCurrEra() {
        return this.currEra;
    }

    public void increaseCurrEra() throws IllegalStateException{
        if(this.currEra++ >3) throw new IllegalStateException("era not supported");
    }
}
