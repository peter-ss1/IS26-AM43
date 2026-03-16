package it.polimi.ingsw.am43.Board;
import java.util.ArrayList;
import java.util.Optional;
import it.polimi.ingsw.am43.Players.Player;


public class OfferTrackCard {

    private final char letter;
    private final List<OfferAction> actions;
    private Optional<Player> player;

    public OfferTrackCard(char l,ArrayList<OfferAction> a){
        this.letter=l;
        this.actions=new ArrayList<OfferAction>(a);
        player=Optional.empty();
    }

    public char getLetter() {
        return this.letter;
    }

    public ArrayList<OfferAction> getActions() {
        return new ArrayList<>(this.actions);
    }

    public Optional<Player> getPlayer() {
        return this.player;
    }

    public void setPlayer(Player p) {
        this.player= Optional.of(p);
    }

    public void removePlayer() {
        this.player=Optional.empty();   //unusefull but for explainability
    }
}