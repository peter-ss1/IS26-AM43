package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;

public abstract class TribeCard extends Card {

    public TribeCard(int era,int id) {
        super(era, id);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public abstract void tribeEntranceEffect(Player player);

    @Override
    public OfferAction firstRowChoice() {
        return  OfferAction.BOTTOM;
    }
}
