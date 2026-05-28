package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

public class Shaman extends CharacterCard {
    private final int shamanStars;

    public Shaman(int era, int id, int shamanStars) {
        super(era, id);
        this.shamanStars = shamanStars;
    }

    public int getShamanStars() {
        return shamanStars;
    }

    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterShamanStars(shamanStars);
        player.getTribe().activateTribeBuildings(observer, player);
    }

}
