package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;

public abstract class Card {
    private final int era;
    private final int id;

    public Card(int era, int id) {
        this.era = era;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getEra() {
        return era;
    }

    public abstract boolean isPickable();

    public abstract void addToRow(Row row);

    public abstract OfferAction firstRowChoice();
    public abstract void pick(Player player);

    public abstract boolean isContainedInRow(Row row);
    public abstract void removeFromRow(Row row) throws IllegalArgumentException;

}