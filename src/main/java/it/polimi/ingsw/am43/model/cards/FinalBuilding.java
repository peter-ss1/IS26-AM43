package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

public class FinalBuilding extends Building {
    private final FinalEffect effect;

    public FinalBuilding(int era, int id, int cost, int prestigePoints, FinalEffect effect) {
        super(era, id, cost, prestigePoints);
        this.effect = effect;
    }

    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterFood(-(this.getCost()));
        observer.broadcast(new Update.BuildingBoughtUpdate(player.getNickname(), this.getCost()));
    }

    public void finalBuildingEffect(Player player) {
        this.effect.manifest(player);
    }
}
