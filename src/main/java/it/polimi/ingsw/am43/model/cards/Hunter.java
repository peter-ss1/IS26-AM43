package it.polimi.ingsw.am43.model.cards;

public class Hunter extends CharacterCard {
    private final boolean active;

    public Hunter(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}