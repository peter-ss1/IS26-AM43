package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.player.Player;

public class TimedBuilding extends Building {
    private final TimedEffect effect;

    public TimedBuilding(int era, int id, int cost, int prestigePoints, TimedEffect effect) {
        super(era, id, cost, prestigePoints);
        this.effect = effect;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterFood(-(this.getCost()));
    }

    public void TimedBuildingEffect(Player player, Game game, Board board) {
        this.effect.manifest(player, game, board);
    }
}
