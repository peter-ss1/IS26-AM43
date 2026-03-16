package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

@FunctionalInterface
public interface EventEffect<T extends Event> {
    void manifest(Player player, T event);
}
