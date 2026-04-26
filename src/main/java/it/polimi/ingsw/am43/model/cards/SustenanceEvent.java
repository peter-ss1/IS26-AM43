package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.List;

public class SustenanceEvent extends Event {

    public SustenanceEvent(int era, int id) {
        super(era, id);
    }

    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    @Override
    public void affectPlayers(List<Player> players) {
        for (Player p : players) {
            int amount = p.getTribe().getTribeNumber() - p.getSustenanceDiscount();
            if (amount <= 0) {
                continue;
            }
            int excess = p.getFood() - amount;
            if (excess < 0) {
                p.alterFood(-p.getFood());
                p.alterPrestigePoints(excess * this.getEra());
            }
            else {p.alterFood(-amount);}
            p.getTribe().activateEventBuildings(this, p);
        }
    }

    @Override
    public void triggerBuilding(EventBuilding building, Player player) {
        building.reactToEvent(player, this);
    }
    @Override
    public String toString() {
        return "Evento Sostentamento (Era " + getEra() + ") - Devi spendere cibo pari alla grandezza della tua tribù (meno gli sconti). Se non puoi, perdi PV.";
    }

    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.PURPLE + TextFormat.BOLD + " EV. SOSTENT." + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│             │";
        lines[5] = "│             │";
        lines[6] = "└─────────────┘";
        return lines;
    }
}
