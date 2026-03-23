package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public interface TribeBonus {
    int calculateBonus(Player player);
    void giveBonus(Player player, int bonus);
}
