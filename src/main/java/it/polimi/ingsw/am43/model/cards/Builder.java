package it.polimi.ingsw.am43.model.cards;
import it.polimi.ingsw.am43.model.player.Player;

public class Builder extends CharacterCard {
    private final int buildingDiscount;
    private final int prestigePoints;

    public Builder(int era, int buildingDiscount, int prestigePoints) {
        super(era);
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
        player.getTribe().addCardToTribe(this);
        player.alterBuildingDiscount(buildingDiscount);
    }
}