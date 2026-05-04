package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

public class FinalBuilding extends Building {
    private final FinalEffect effect;

    public FinalBuilding(int era, int id, int cost, int prestigePoints, FinalEffect effect) {
        super(era, id, cost, prestigePoints);
        this.effect = effect;
    }

    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterFood(-(this.getCost()));
        observer.broadcast(new Update.BuildingBoughtUpdate(player.getNickname(), this.getCost()));
    }

    public void finalBuildingEffect(Player player) {
        this.effect.manifest(player);
    }
    @Override
    public String toString() {
        return "Edificio Finale (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Attiva il suo effetto alla fine della partita.";
    }

    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.BLUE + TextFormat.BOLD + " EDIF. FINALE" + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│ Costo: " + getCost() + "    │";
        lines[5] = "│ PP: " + getPrestigePoints() + "       │";
        lines[6] = "└─────────────┘";
        return lines;
    }
}
