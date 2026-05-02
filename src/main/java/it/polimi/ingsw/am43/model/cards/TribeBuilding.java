package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

public class TribeBuilding extends Building {
    private final TribeBonus bonus;
    private int lastGivenBonus;

    public TribeBuilding(int era, int id, int cost, int prestigePoints, TribeBonus bonus) {
        super(era, id, cost, prestigePoints);
        this.bonus = bonus;
        this.lastGivenBonus = 0;
    }

    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterFood(-(this.getCost()));
        lastGivenBonus = this.bonus.calculateBonus(player);
        observer.broadcast(new Update.BuildingBoughtUpdate(player.getNickname(),  this.getCost()));
    }

    public void tribeBuildingEffect(GameObserver observer, Player player) {
        int newBonus = this.bonus.calculateBonus(player);
        bonus.giveBonus(observer, player, newBonus - this.lastGivenBonus);
        this.lastGivenBonus = newBonus;
    }
}
