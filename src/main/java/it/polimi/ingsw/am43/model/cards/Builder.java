package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

public class Builder extends CharacterCard {
    private final int buildingDiscount;
    private final int prestigePoints;

    public Builder(int era, int id, int buildingDiscount, int prestigePoints) {
        super(era, id);
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
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterBuildingDiscount(buildingDiscount);
        player.getTribe().activateTribeBuildings(observer, player);
    }
}