package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.player.Player;

public class Inventor extends CharacterCard {
    private InventorSymbol symbol;

    public Inventor(int era,int id, InventorSymbol symbol) {
        super(era,id, "Inventor");
        this.symbol = symbol;
    }

    public InventorSymbol getSymbol() {
        return symbol;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        // effetto specifico
    }

}
