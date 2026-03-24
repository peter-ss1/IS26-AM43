package it.polimi.ingsw.am43.model.cards;

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
}
