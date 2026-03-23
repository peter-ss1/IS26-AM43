package it.polimi.ingsw.am43.model.cards;
import it.polimi.ingsw.am43.model.player.Player;

public class Artist extends CharacterCard {

    public Artist(int era) {
        super(era);
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addCardToTribe(this);
    }


}