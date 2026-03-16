package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.*;

import java.util.ArrayList;

public abstract class Event extends Card {

    public Event(int era) {
        super(era);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public abstract void affectPlayers(ArrayList<Player> players);

    public abstract void triggerBuilding(EventBuilding building, Player player);

}
