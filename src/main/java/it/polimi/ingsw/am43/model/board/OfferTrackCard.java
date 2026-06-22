package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * A single slot of the offer track. It carries a fixed set of actions and,
 * optionally, the player who placed their totem on it.
 */
public class OfferTrackCard implements Serializable {

    private final List<OfferAction> actions;
    private Player player;

    /**
     * @param a the actions granted by this slot
     */
    public OfferTrackCard(List<OfferAction> a) {
        this.actions = new ArrayList<>(a);
        player = null;
    }

    /** @return a copy of the actions granted by this slot */
    public ArrayList<OfferAction> getActions() {
        return new ArrayList<>(this.actions);
    }

    /** @return the player on this slot, or empty if it is free */
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(this.player);
    }

    /**
     * Places a player on this slot and assigns them its actions.
     *
     * @param p the player to place
     */
    public void setPlayer(Player p) {
        this.player = p;
        p.setAvailableActions(this.getActions());
    }

    /** Frees the slot, removing the player currently on it. */
    public void removePlayer() {
        this.player = null;
    }

}