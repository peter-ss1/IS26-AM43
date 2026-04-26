package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;

public class Hunter extends CharacterCard {
    private final boolean active;

    public Hunter(int era, int id, boolean active) {
        super(era, id);
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addCardToTribe(this);
        if (!active) {
            return;
        }
        player.alterFood(player.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
        player.getTribe().activateTribeBuildings(player);

    }
    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.RED + TextFormat.BOLD + " CACCIATORE  " + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│ " + (isActive() ? "Attivo      " : "Inattivo    ") + "│";
        lines[5] = "│             │";
        lines[6] = "└─────────────┘";
        return lines;
    }
}