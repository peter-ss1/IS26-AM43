package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.player.Player;

public class HuntEventBuilding extends EventBuilding {
    public final EventEffect<HuntEvent> reaction;

    public HuntEventBuilding(int era, int id, int cost, int prestigePoints, EventEffect<HuntEvent> reaction) {
        super(era, id, cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(Player player, HuntEvent event) {
        reaction.manifest(player, event);
    }
    @Override
    public String toString() {
        return "Edificio Evento Caccia (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Reagisce quando si risolve un Evento Caccia.";
    }

    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.BLUE + TextFormat.BOLD + " EDIF. CACCIA" + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│ Costo: " + getCost() + "    │";
        lines[5] = "│ PP: " + getPrestigePoints() + "       │";
        lines[6] = "└─────────────┘";
        return lines;
    }
}
