package it.polimi.ingsw.am43.model.cards;

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
}