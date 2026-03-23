package it.polimi.ingsw.am43.model.cards;
import it.polimi.ingsw.am43.model.player.Player;

public class Artist extends CharacterCard {

    public Artist(int era, int id) {
        super(era,id, "Artist");
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        // effetto specifico
    }


}