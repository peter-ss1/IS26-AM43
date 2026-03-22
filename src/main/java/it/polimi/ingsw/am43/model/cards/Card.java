package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.enums.OfferAction;

public abstract class Card {
    private final int era;

    public Card(int era) {
        this.era = era;
    }

    public int getEra() {
        return era;
    }

    public abstract boolean isPickable();

    public abstract void addToRow(Row row);

    public abstract OfferAction firstRowChoice();
}