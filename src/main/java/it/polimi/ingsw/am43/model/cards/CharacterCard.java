package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

/**
 * Base class for tribe cards that represent characters.
 */
public abstract class CharacterCard extends TribeCard {

    /**
     * Creates a {@code CharacterCard} with the specified era and identifier.
     *
     * @param era the era associated with the card
     * @param id the identifier of the card
     */
    public CharacterCard(int era, int id) {
        super(era, id);
    }

    /**
     * Adds this character card to the card collection of the specified row.
     *
     * @param row the row that receives this card
     */
    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    /**
     * Broadcasts the card pickup and applies this card's tribe entrance effect.
     *
     * @param observer the game observer used to broadcast the pickup update
     * @param player the player who picks this card
     */
    @Override
    public void pick(GameObserver observer, Player player) {
        observer.broadcast(new Update.CardPickedUpdate(player.getNickname(), this.getId(), player.getAvailableActions().size()-1 == 0));
        this.tribeEntranceEffect(observer, player);
    }

    /**
     * Checks whether this character card is contained in the specified row.
     *
     * @param row the row to inspect
     * @return {@code true} if the row contains this card, otherwise {@code false}
     */
    @Override
    public boolean isContainedInRow(Row row){
        return row.contains(this);
    }

    /**
     * Removes this character card from the specified row.
     *
     * @param row the row from which this card is removed
     */
    @Override
    public void removeFromRow(Row row) {
        row.removeCard(this);
    }

}
