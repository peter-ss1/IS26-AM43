package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;

public abstract class TribeCard extends Card {

    public TribeCard(int era) {
        super(era);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public abstract void tribeEntranceEffect(Player player);

    @Override
    public void rowAction(Row row) {}

    @Override
    public void firstRowAction(Game game) {

    }
}
