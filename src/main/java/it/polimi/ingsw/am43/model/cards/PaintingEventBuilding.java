package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public class PaintingEventBuilding extends EventBuilding {
    public final EventEffect<PaintingEvent> reaction;

    public PaintingEventBuilding(int era, int cost, int prestigePoints, EventEffect<PaintingEvent> reaction) {
        super(era, cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(Player player, PaintingEvent event) {reaction.manifest(player, event);}
}
