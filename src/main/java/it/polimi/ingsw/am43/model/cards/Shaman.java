package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public class Shaman extends CharacterCard {
    private final int shamanStars;

    public Shaman(int era, int shamanStars) {
        super(era);
        this.shamanStars = shamanStars;
    }

    public int getShamanStars() {
        return shamanStars;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterShamanStars(shamanStars);
    }

}
