package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public class Gatherer extends CharacterCard {

    public Gatherer(int era,int id) {
        super(era,id, "Gatherer");
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        // effetto specifico
    }
}
