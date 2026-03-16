package it.polimi.ingsw.am43;

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
