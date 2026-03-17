package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.player.Player;

public class TribeBuilding extends Building {
    private final TribeBonus bonus;
    private int lastGivenBonus;

    public TribeBuilding(int era, int cost, int prestigePoints, TribeBonus bonus) {
        super(era,cost, prestigePoints);
        this.bonus = bonus;
        this.lastGivenBonus = 0;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addBuilding(this);
        player.alterFood(-(this.getCost()));
        lastGivenBonus = this.bonus.giveBonus(player);
    }

    @Override
    public InsertionStrategy getInsertionStrategy() {
        return null;
    }

    public void tribeBuildingEffect(Player player) {
        player.alterFood(this.bonus.giveBonus(player) - this.lastGivenBonus);
    }

    @Override
    public void firstRowAction(Game game) {

    }
}
