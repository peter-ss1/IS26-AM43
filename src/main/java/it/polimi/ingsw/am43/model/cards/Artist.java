package it.polimi.ingsw.am43.model.cards;
import it.polimi.ingsw.am43.model.player.Player;

public class Artist extends CharacterCard {

    public Artist(int era) {
        super(era, "Artist");
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        // effetto specifico
    }


}