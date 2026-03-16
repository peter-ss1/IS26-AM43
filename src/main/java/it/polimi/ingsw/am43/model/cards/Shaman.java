package it.polimi.ingsw.am43.model.cards;

public class Shaman extends CharacterCard {
    private final int shamanStars;

    public Shaman(int shamanStars) {
        this.shamanStars = shamanStars;
    }

    public int getShamanStars() {
        return shamanStars;
    }

}
