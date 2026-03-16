package it.polimi.ingsw.am43.model.cards;

public class Artist extends CharacterCard {
    public Artist(int era) {
        super(era);
    }

    @Override
    public InsertionStrategy getInsertionStrategy() {
        return () -> characters.addKey("ARTIST");
    }
}