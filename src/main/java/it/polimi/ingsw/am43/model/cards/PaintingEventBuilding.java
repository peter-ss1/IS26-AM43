package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

public class PaintingEventBuilding extends EventBuilding {
    public final EventEffect<PaintingEvent> reaction;

    public PaintingEventBuilding(int era, int id, int cost, int prestigePoints, EventEffect<PaintingEvent> reaction) {
        super(era, id, cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(GameObserver observer, Player player, PaintingEvent event) {
        reaction.manifest(observer, player, event);
    }
}
