package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.player.Player;

public class SustenanceEventBuilding extends EventBuilding {
    public final EventEffect<SustenanceEvent> reaction;

    public SustenanceEventBuilding(int era, int id, int cost, int prestigePoints, EventEffect<SustenanceEvent> reaction) {
        super(era, id, cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(Player player, SustenanceEvent event) {
        reaction.manifest(player, event);
    }
    @Override
    public String toString() {
        return "Edificio Evento Sostentamento (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Reagisce quando si risolve un Evento Sostentamento.";
    }

    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.BLUE + TextFormat.BOLD + " EDIF. SOSTEN" + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│ Costo: " + getCost() + "    │";
        lines[5] = "│ Pp: " + getPrestigePoints() + "       │";
        lines[6] = "└─────────────┘";
        return lines;
    }

}
