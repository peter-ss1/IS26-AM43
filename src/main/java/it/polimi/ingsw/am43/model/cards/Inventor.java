package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

public class Inventor extends CharacterCard {
    private final InventorSymbol symbol;

    public Inventor(int era, int id, InventorSymbol symbol) {
        super(era, id);
        this.symbol = symbol;
    }

    public InventorSymbol getSymbol() {
        return symbol;
    }

    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.getTribe().activateTribeBuildings(observer, player);
    }

}
