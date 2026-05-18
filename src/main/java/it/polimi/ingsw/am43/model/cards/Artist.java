package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

public class Artist extends CharacterCard {

    public Artist(int era, int id) {
        super(era, id);
    }

    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.getTribe().activateTribeBuildings(observer, player);
    }
}