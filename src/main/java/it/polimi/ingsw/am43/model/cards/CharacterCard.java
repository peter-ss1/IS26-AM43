package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

public abstract class CharacterCard extends TribeCard {

    public CharacterCard(int era, int id) {
        super(era, id);
    }

    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    @Override
    public void pick(GameObserver observer, Player player) {
        observer.broadcast(new Update.CardPickedUpdate(player.getNickname(), this.getId()));
        this.tribeEntranceEffect(observer, player);
    }

    @Override
    public boolean isContainedInRow(Row row){
        return row.contains(this);
    }

    @Override
    public void removeFromRow(Row row) {
        row.removeCard(this);
    }

}
