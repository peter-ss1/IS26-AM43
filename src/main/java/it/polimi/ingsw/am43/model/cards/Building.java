package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

/**
 * Base class for every building card. A building has a food cost and grants
 * prestige points; concrete subclasses define the effect triggered when it
 * enters the tribe.
 */
public abstract class Building extends TribeCard {
    private final int cost;
    private final int prestigePoints;

    /**
     * @param era            the era the building belongs to
     * @param id             the unique id of the building
     * @param cost           the food cost to pick the building
     * @param prestigePoints the prestige points the building grants
     */
    public Building(int era, int id, int cost, int prestigePoints) {
        super(era, id);
        this.cost = cost;
        this.prestigePoints = prestigePoints;
    }

    /** {@inheritDoc} Adds this building to the given row. */
    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    /** @return the food cost to pick the building */
    public int getCost() {
        return cost;
    }

    /** @return the prestige points the building grants */
    public int getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * {@inheritDoc}
     * Checks the player can afford the building (cost minus building discount),
     * notifies the pickup and applies the tribe-entrance effect.
     *
     * @throws IllegalMoveException if the player does not have enough food
     */
    @Override
    public void pick(GameObserver observer, Player player) {
        if (player.getFood() < this.cost - player.getBuildingDiscount()) throw new IllegalMoveException("Cannot pick building with insufficient food");
        observer.broadcast(new Update.CardPickedUpdate(player.getNickname(), this.getId(), player.getAvailableActions().size()-1 == 0));
        this.tribeEntranceEffect(observer, player);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isContainedInRow(Row row){
        return row.contains(this);
    }
    /** {@inheritDoc} */
    @Override
    public void removeFromRow(Row row) {
        row.removeCard(this);
    }

}