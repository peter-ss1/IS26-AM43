package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.player.Player;

@FunctionalInterface
public interface TimedEffect {
    void manifest(Player player, Game game);
}
