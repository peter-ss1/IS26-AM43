package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.List;

public class SustenanceEvent extends Event {

    public SustenanceEvent(int era,int id) {
        super(era,id);
    }

    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    @Override
    public void affectPlayers(List<Player> players) {
        for (Player p : players) {
            int amount = p.getTribe().getTribeNumber()-p.getSustenanceDiscount();
            if (amount <= 0) { return;}
            int excess = p.getFood() - amount;
            p.alterFood(-amount);
            if (excess < 0) {
                p.alterPrestigePoints(excess * this.getEra());
            }
            p.getTribe().activateEventBuildings(this, p);
        }
    }

    @Override
    public void triggerBuilding(EventBuilding building, Player player) {
        building.reactToEvent(player, this);
    }
}
