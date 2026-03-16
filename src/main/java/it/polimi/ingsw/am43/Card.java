package it.polimi.ingsw.am43;

public abstract class Card {
    private final int era;

    public Card(int era) {
        this.era = era;
    }

    public int getEra() {
        return era;
    }

    public abstract boolean isPickable();

    public abstract void rowAction(Row row);

    public abstract void firstRowAction(Game game);
}