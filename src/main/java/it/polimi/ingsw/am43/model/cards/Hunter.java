package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.board.Row;

public class Hunter extends CharacterCard {
    private final boolean active;

    public Hunter(int era, boolean active) {
        super(era);
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
        } player.alterFood(player.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
    }
}