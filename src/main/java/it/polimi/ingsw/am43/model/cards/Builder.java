package it.polimi.ingsw.am43.model.cards;

public class Builder extends CharacterCard {
    private final int buildingDiscount;
    private final int prestigePoints;

    public Builder(int buildingDiscount, int prestigePoints) {
        this.buildingDiscount = buildingDiscount;
        this.prestigePoints = prestigePoints;
    }
}