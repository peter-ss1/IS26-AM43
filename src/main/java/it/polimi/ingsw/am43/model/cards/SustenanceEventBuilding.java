package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

public class SustenanceEventBuilding extends EventBuilding {
    public final EventEffect<SustenanceEvent> reaction;

    public SustenanceEventBuilding(int era, int id, int cost, int prestigePoints, EventEffect<SustenanceEvent> reaction) {
        super(era, id, cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(GameObserver observer, Player player, SustenanceEvent event) {
        reaction.manifest(observer, player, event);
    }
    @Override
    public String toString() {
        return "Edificio Evento Sostentamento (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Reagisce quando si risolve un Evento Sostentamento.";
    }

}
