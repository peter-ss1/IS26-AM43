package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public class Shaman extends CharacterCard {
    private int shamanStars;

    public Shaman(int era, int shamanStars) {
        super(era, "Shaman");
        this.shamanStars = shamanStars;
    }

    public int getShamanStars() {
        return shamanStars;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        // effetto specifico
    }

}
