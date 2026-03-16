package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

@FunctionalInterface
public interface InsertionStrategy {
    void insert(Player player);
}
