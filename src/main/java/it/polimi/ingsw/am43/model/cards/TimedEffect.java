package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.player.Player;
import java.io.IOException;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classEvent")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimedEffect.BonusTurnFood.class, name = "bonusTurnFood"),
        @JsonSubTypes.Type(value = TimedEffect.BonusPickCard.class, name = "bonusPickCard")
})


@FunctionalInterface
public interface TimedEffect {
    void manifest(Player player, Game game, Board board);

    public static class BonusTurnFood implements TimedEffect {

        @Override
        public void manifest(Player player, Game game, Board board) {
            if (game.getPhase() == GamePhase.ACTION_RESOLUTION && player == game.getCurrPlayer() /*&& game.isTurnOrderNotFull*/) {
                player.alterFood(1);
            }
        }
    }

    public static class BonusPickCard implements TimedEffect {

        @Override
        public void manifest(Player player, Game game, Board board) {
            if (game.getPhase() == GamePhase.ACTION_RESOLUTION /*&& game.isTurnOrderFull*/) {
                game.setPhase(GamePhase.DRAW_FROM_TOP_BONUS_ACTION);
                game.setCurrPlayer(player);
                game.getPhase().resolvePhase(game, board);

            }
        }
    }
}
