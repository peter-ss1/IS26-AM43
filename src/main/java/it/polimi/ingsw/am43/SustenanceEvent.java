package it.polimi.ingsw.am43;

import java.util.ArrayList;

public class SustenanceEvent extends Event {

    public SustenanceEvent(int era) {
        super(era);
    }

    @Override
    public void affectPlayers(ArrayList<Player> players) {
        for (Player p : players) {
            int amount = p.getTribeNumber()-p.getSustenanceDiscount();
            int excess = p.getFood() - amount;
            p.alterFood(-amount);
            p.alterPrestigePoints(-(excess * this.getEra()));
        }
    }

    @Override
    public void triggerBuilding(EventBuilding building, Player player) {
        building.reactToEvent(player, this);
    }
}
