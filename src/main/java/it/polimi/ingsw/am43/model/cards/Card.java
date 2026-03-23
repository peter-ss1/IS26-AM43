package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.enums.OfferAction;

public abstract class Card {
    private final int era;
    private final int id;

    public Card(int era, int id) {
        this.era = era;
        this.id = id;
    }

    public int getEra() {
        return era;
    }

    public abstract boolean isPickable();

    public abstract void addToRow(Row row);

    public abstract OfferAction firstRowChoice();
}