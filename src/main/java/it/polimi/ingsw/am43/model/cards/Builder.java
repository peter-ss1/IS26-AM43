package it.polimi.ingsw.am43.model.cards;
import it.polimi.ingsw.am43.model.player.Player;

public class Builder extends CharacterCard {
    private int buildingDiscount;
    private int prestigePoints;

    public Builder(int era, int buildingDiscount, int prestigePoints) {
        super(era, "Builder");
        this.buildingDiscount = buildingDiscount;
        this.prestigePoints = prestigePoints;
    }

    public int getBuildingDiscount() {
        return buildingDiscount;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        // effetto specifico
    }
}