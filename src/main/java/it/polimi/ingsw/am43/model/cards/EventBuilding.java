package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public abstract class EventBuilding extends Building {

    public EventBuilding(int era, int id, int cost, int prestigePoints) {
        super(era,id, cost, prestigePoints);
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterFood(-(this.getCost()));
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
