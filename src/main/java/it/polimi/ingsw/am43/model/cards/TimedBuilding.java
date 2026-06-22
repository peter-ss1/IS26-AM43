package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

/**
 * A building whose effect is triggered at specific moments of a round,
 * according to the {@link TimedEffect} strategy it carries.
 */
public class TimedBuilding extends Building {
    private final TimedEffect effect;

    /**
     * @param era            the era the building belongs to
     * @param id             the unique id of the building
     * @param cost           the food cost to pick the building
     * @param prestigePoints the prestige points the building grants
     * @param effect         the effect triggered during the round
     */
    public TimedBuilding(int era, int id, int cost, int prestigePoints, TimedEffect effect) {
        super(era, id, cost, prestigePoints);
        this.effect = effect;
    }

    /**
     * {@inheritDoc}
     * Adds the building to the tribe and charges its cost (after the building discount).
     */
    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        int cost = this.getCost() - player.getBuildingDiscount();
        if (cost<0) cost = 0;
        player.alterFood(-cost);
        observer.broadcast(new Update.BuildingBoughtUpdate(player.getNickname(), cost));
    }

    /**
     * Applies the building's timed effect, evaluating the current game state.
     *
     * @param player the owner of the building
     * @param game   the current game
     * @param board  the current board
     */
    public void TimedBuildingEffect(Player player, Game game, Board board) {
        this.effect.manifest(player, game, board);
    }

}
