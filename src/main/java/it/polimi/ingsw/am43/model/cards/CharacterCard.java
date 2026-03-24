package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;

public abstract class CharacterCard extends TribeCard {

    public CharacterCard(int era, int id) {
        super(era, id);
    }

    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    @Override
    public void pick(Player player) {
        this.tribeEntranceEffect(player);
    }
}
