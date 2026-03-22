package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public class FinalBuilding extends Building {
    private final FinalEffect effect;

    public FinalBuilding(int era, int cost, int prestigePoints, FinalEffect effect) {
        super(era, cost, prestigePoints);
        this.effect = effect;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterFood(-(this.getCost()));
    }

    public void finalBuildingEffect(Player player) {
        this.effect.manifest(player);
    }
}
