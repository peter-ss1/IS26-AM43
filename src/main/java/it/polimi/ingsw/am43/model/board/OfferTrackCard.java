package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OfferTrackCard {

    private final List<OfferAction> actions;
    private Player player;

    public OfferTrackCard(List<OfferAction> a) {
        this.actions = new ArrayList<>(a);
        player = null;
    }

    public ArrayList<OfferAction> getActions() {
        return new ArrayList<>(this.actions);
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(this.player);
    }

    public void setPlayer(Player p) {
        this.player = p;
        p.setAvailableActions(this.getActions());
    }

    public void removePlayer() {
        this.player = null;
    }

}