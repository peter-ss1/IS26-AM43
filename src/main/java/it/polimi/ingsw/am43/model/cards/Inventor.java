package it.polimi.ingsw.am43.model.cards;

public class Inventor extends CharacterCard {
    private final char symbol;

    public Inventor(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

}
