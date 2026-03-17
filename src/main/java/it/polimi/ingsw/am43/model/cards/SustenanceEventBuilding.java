package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public class SustenanceEventBuilding extends EventBuilding {
    public final EventEffect<SustenanceEvent> reaction;

    public SustenanceEventBuilding(int era, int cost, int prestigePoints, EventEffect<SustenanceEvent> reaction) {
        super(era, cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(Player player, SustenanceEvent event) {reaction.manifest(player, event);}
}
