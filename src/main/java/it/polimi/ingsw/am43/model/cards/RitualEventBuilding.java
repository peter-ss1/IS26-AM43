package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

public class RitualEventBuilding extends EventBuilding {
    public final EventEffect<RitualEvent> reaction;

    public RitualEventBuilding(int era,int id, int cost, int prestigePoints, EventEffect<RitualEvent> reaction) {
        super(era, id,cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(GameObserver observer, Player player, RitualEvent event) {
        reaction.manifest(observer, player, event);
    }
    @Override
    public String toString() {
        return "Edificio Evento Rituale (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Reagisce quando si risolve un Evento Rituale.";
    }

    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.BLUE + TextFormat.BOLD + " EDIF. RITUAL" + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│ Costo: " + getCost() + "    │";
        lines[5] = "│ PP: " + getPrestigePoints() + "       │";
        lines[6] = "└─────────────┘";
        return lines;
    }
}
