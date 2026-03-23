package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.*;

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

    public abstract void rowAction(Row row);

    public abstract void firstRowAction(Game game);
}