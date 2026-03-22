package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.List;

public abstract class Event extends Card {

    public Event(int era) {
        super(era);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    @Override
    public OfferAction firstRowChoice() {
        return OfferAction.TOP;
    }

    public abstract void affectPlayers(List<Player> players);

    public abstract void triggerBuilding(EventBuilding building, Player player);

}
