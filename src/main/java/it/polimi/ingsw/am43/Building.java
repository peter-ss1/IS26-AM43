package it.polimi.ingsw.am43;

public abstract class Building extends TribeCard {
    private final int cost;
    private final int prestigePoints;

    public Building(int era, int cost, int prestigePoints) {
        super(era);
        this.cost = cost;
        this.prestigePoints = prestigePoints;
    }

    public int getCost() {
        return cost;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }
}