package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy describing the round-time effect of a {@link TimedBuilding}. Each
 * concrete implementation activates a bonus in a specific game phase.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classEvent")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimedEffect.BonusTurnFood.class, name = "bonusTurnFood"),
        @JsonSubTypes.Type(value = TimedEffect.BonusPickCard.class, name = "bonusPickCard")
})



@FunctionalInterface
public interface TimedEffect extends Serializable {
    /**
     * Applies this timed effect, inspecting the current game state to decide
     * whether and how to act.
     *
     * @param player the owner of the building
     * @param game   the current game
     * @param board  the current board
     */
    void manifest(Player player, Game game, Board board);

    /**
     * Grants one extra food during action resolution when the current player
     * acts while a food bonus is available and the order queue is not full.
     */
    public static class BonusTurnFood implements TimedEffect {

        /** {@inheritDoc} */
        @Override
        public void manifest(Player player, Game game, Board board) {
            if (game.getPhase() == GamePhase.ACTION_RESOLUTION && player == game.getCurrPlayer() && !board.isOrderQueueFull() && board.hasFoodBonus()) {
                player.alterFood(1);
                game.getObserver().broadcast(new Update.BuildingEffectUpdate(player.getNickname(), 1, "food"));
            }
        }
    }

    /**
     * Grants an additional draw action from the top row at the end of a round
     * when the order queue is full.
     */
    public static class BonusPickCard implements TimedEffect {

        /** {@inheritDoc} */
        @Override
        public void manifest(Player player, Game game, Board board) {
            if (game.getPhase() == GamePhase.ROUND_ENDING && board.isOrderQueueFull()) {
                game.setPhase(GamePhase.DRAW_FROM_TOP_BONUS_ACTION);
                List<OfferAction> bonusAction = new ArrayList<>();
                bonusAction.add(OfferAction.TOP);
                player.setAvailableActions(bonusAction);
                game.getObserver().broadcast(new Update.BuildingEffectUpdate(player.getNickname(), 1, "additional action"));
                game.setCurrPlayer(player);
                if (!board.pickableCards(OfferAction.TOP, player)) {
                    game.getPhase().resolvePhase(game, board);
                }
            }
        }
    }
}
