package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.player.Player;

public class Artist extends CharacterCard {

    public Artist(int era, int id) {
        super(era, id);
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
        lines[1] = "│" + TextFormat.YELLOW + TextFormat.BOLD + " ARTISTA     " + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│             │";
        lines[5] = "│             │";
        lines[6] = "└─────────────┘";
        return lines;
    }
}