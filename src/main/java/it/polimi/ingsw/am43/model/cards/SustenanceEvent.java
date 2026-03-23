package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

import java.util.ArrayList;

public class SustenanceEvent extends Event {

    public SustenanceEvent(int era,int id) {
        super(era,id);
    }

    @Override
    public void affectPlayers(ArrayList<Player> players) {
        for (Player p : players) {
            int amount = p.getTribe().getTribeNumber()-p.getSustenanceDiscount();
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
