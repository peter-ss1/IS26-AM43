package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;

public abstract class Building extends TribeCard {
    private final int cost;
    private final int prestigePoints;

    public Building(int era, int id, int cost, int prestigePoints) {
        super(era, id);
        this.cost = cost;
        this.prestigePoints = prestigePoints;
    }

    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    public int getCost() {
        return cost;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    @Override
    public void pick(Player player) {
        if (player.getFood() - player.getBuildingDiscount() < this.cost) throw new IllegalArgumentException("Insufficient Food");
        this.tribeEntranceEffect(player);
    }
}