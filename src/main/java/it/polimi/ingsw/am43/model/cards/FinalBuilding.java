package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;

public class FinalBuilding extends Building {
    private final FinalEffect effect;

    public FinalBuilding(int era,int id, int cost, int prestigePoints, FinalEffect effect) {
        super(era,id,cost, prestigePoints);
        this.effect = effect;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addBuilding(this);
        player.alterFood(-(this.getCost()));
    }

    @Override
    public InsertionStrategy getInsertionStrategy() {
        return null;
    }

    public void finalBuildingEffect(Player player) {
       this.effect.manifest(player);
    }

    @Override
    public void rowAction(Row row) {

    }

    @Override
    public void firstRowAction(Game game) {

    }
}
