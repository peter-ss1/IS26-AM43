package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

import java.util.ArrayList;

public class HuntEvent extends Event {

    public HuntEvent(int era) {
        super(era);
    }

    @Override
    public void affectPlayers(ArrayList<Player> players) {
        for (Player p : players) {
            int amount = p.getHunterNumber();
            p.alterFood(amount);
            p.alterPrestigePoints(amount*this.getEra());
            p.getTribe().activateEventBuildings(this);
        }
    }

    @Override
    public void triggerBuilding(EventBuilding building, Player player) {
        building.reactToEvent(player, this);
    }
}
