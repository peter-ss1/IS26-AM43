package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.player.Player;

public class Inventor extends CharacterCard {
    private InventorSymbol symbol;

    public Inventor(int era, int id, InventorSymbol symbol) {
        super(era, id);
        this.symbol = symbol;
    }

    public InventorSymbol getSymbol() {
        return symbol;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addCardToTribe(this);
        player.getTribe().activateTribeBuildings(player);
    }
    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.CYAN + TextFormat.BOLD + " INVENTORE   " + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";

        String sym = symbol.toString();
        sym = sym.length() > 11 ? sym.substring(0, 11) : sym;
        lines[4] = String.format("│ %-11s │", sym);
        lines[5] = "│             │";
        lines[6] = "└─────────────┘";
        return lines;
    }

}
