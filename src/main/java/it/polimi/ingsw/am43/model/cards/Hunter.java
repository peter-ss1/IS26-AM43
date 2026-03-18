package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public class Hunter extends CharacterCard {
    private boolean active;

    public Hunter(int era, boolean active) {
        super(era, "Hunter");
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        // effetto specifico
    }
}