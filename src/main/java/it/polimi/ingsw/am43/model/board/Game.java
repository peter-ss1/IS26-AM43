package it.polimi.ingsw.am43.model.board;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


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


    public Game(int np, String nk) throws IOException {
        this.availableColors = new ArrayList<>(Arrays.asList(Color.values()));
        this.players=new ArrayList<>();
        this.addPlayer(nk);
        this.numPlayers=np;
        this.phase= GamePhase.PREPARATION;
        this.board=new Board();
    }


    public ArrayList<Color> getAvailableColors() {
        return new ArrayList<>(this.availableColors);
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

    public void placePlayerOnTrack(String name, int position){
        Player np= players.stream().filter(p->p.getNickname().equals(name)).findFirst().orElseThrow(()->new IllegalArgumentException("PLayer not registered"));
        this.board.setPlayerOnTrack(np,position);
    }

    public void pickCard(OfferAction r, int id,String cardClass, String namePlayer)throws IllegalArgumentException{
        switch (r){
            case TOP:{

            }
            case BOTTOM:{

            }
            default: throw new IllegalArgumentException("row not exist");
        }
    }
}
