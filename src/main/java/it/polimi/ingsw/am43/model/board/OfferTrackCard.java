package it.polimi.ingsw.am43.model.board;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.*;


public class OfferTrackCard {

    private final List<OfferAction> actions;
    private Optional<Player> player;

    public OfferTrackCard(ArrayList<OfferAction> a){
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
        this.player=Optional.empty();
    }
}