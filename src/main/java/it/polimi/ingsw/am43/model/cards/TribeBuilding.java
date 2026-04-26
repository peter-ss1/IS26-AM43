package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.player.Player;

public class TribeBuilding extends Building {
    private final TribeBonus bonus;
    private int lastGivenBonus;

    public TribeBuilding(int era, int id, int cost, int prestigePoints, TribeBonus bonus) {
        super(era, id, cost, prestigePoints);
        this.bonus = bonus;
        this.lastGivenBonus = 0;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterFood(-(this.getCost()));
        lastGivenBonus = this.bonus.calculateBonus(player);
    }

    public void tribeBuildingEffect(Player player) {
        int newBonus = this.bonus.calculateBonus(player);
        bonus.giveBonus(player, newBonus - this.lastGivenBonus);
        this.lastGivenBonus = newBonus;
    }
    @Override
    public String toString() {
        return "Edificio Tribù (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Fornisce bonus in base ai personaggi nella tua tribù.";
    }

    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.BLUE + TextFormat.BOLD + " EDIF. TRIBU'" + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│ Costo: " + getCost() + "    │";
        lines[5] = "│ PP: " + getPrestigePoints() + "       │";
        lines[6] = "└─────────────┘";
        return lines;
    }
}
