package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.*;

public abstract class EventBuilding extends Building {

    public EventBuilding(int era, int cost, int prestigePoints) {
        super(era, cost, prestigePoints);
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addBuilding(this);
        player.alterFood(-(this.getCost()));
    }

    @Override
    public InsertionStrategy getInsertionStrategy() {
        return null;
    }

    public void reactToEvent(Player player, SustenanceEvent event) {
    }

    public void reactToEvent(Player player, HuntEvent event) {
    }

    public void reactToEvent(Player player, PaintingEvent event) {

    }

    public void reactToEvent(Player player, RitualEvent event) {

    }
}
