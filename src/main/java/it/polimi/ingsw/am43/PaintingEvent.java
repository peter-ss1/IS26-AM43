package it.polimi.ingsw.am43;

import java.util.ArrayList;

public class PaintingEvent extends Event {

    public PaintingEvent(int era) {
        super(era);
    }

    @Override
    public void affectPlayers(ArrayList<Player> players) {
        for (Player p : players) {
            int amount = p.getArtistNumber();
            if (amount < this.getEra()) {
                p.alterFood(-2);
            }
            else {
                p.alterPrestigePoints(amount*this.getEra());
            }
            p.getTribe().activateEventBuildings(this);
        }
    }

    @Override
    public void triggerBuilding(EventBuilding building, Player player) {
        building.reactToEvent(player, this);
    }
}
