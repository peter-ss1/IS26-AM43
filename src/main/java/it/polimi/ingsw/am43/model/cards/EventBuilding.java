package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

public abstract class EventBuilding extends Building {

    public EventBuilding(int era, int id, int cost, int prestigePoints) {
        super(era,id, cost, prestigePoints);
    }

    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterFood(-(this.getCost()));
        observer.broadcast(new Update.BuildingBoughtUpdate(player.getNickname(), this.getCost()));
    }

    public void reactToEvent(GameObserver observer, Player player, SustenanceEvent event) {
    }

    public void reactToEvent(GameObserver observer, Player player, HuntEvent event) {
    }

    public void reactToEvent(GameObserver observer, Player player, PaintingEvent event) {
    }

    public void reactToEvent(GameObserver observer, Player player, RitualEvent event) {
    }
}
