package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.player.Player;

public class Gatherer extends CharacterCard {

    public Gatherer(int era, int id) {
        super(era, id);
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterSustenanceDiscount(3);
        player.getTribe().activateTribeBuildings(player);
    }
    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.GREEN + TextFormat.BOLD + " RACCOGLITORE" + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│ +3 Sconto   │";
        lines[5] = "│ Sostentam.  │";
        lines[6] = "└─────────────┘";
        return lines;
    }
}
