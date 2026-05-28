package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

public class HuntEventBuilding extends EventBuilding {
    public final EventEffect<HuntEvent> reaction;

    public HuntEventBuilding(int era, int id, int cost, int prestigePoints, EventEffect<HuntEvent> reaction) {
        super(era, id, cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(GameObserver observer, Player player, HuntEvent event) {
        reaction.manifest(observer, player, event);
    }
    @Override
    public String toString() {
        return "Edificio Evento Caccia (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Reagisce quando si risolve un Evento Caccia.";
    }
}
