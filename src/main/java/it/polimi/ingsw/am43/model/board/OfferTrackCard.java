package it.polimi.ingsw.am43.model.board;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.*;


public class OfferTrackCard {

    private final ArrayList<OfferAction> actions;
    private Player player;
    private List<OfferAction> remainingActions;

    public OfferTrackCard(ArrayList<OfferAction> a){
        this.actions=new ArrayList<OfferAction>(a);
        player=null;
    }

    public ArrayList<OfferAction> getActions() {
        return new ArrayList<>(this.actions);
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(this.player);
    }

    public void setPlayer(Player p) {
        this.player= p;
        p.setAvailableActions(this.actions);
    }

    public void removePlayer() {
        this.player=null;
    }

}