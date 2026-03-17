package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public class RitualEventBuilding extends EventBuilding {
    public final EventEffect<RitualEvent> reaction;

    public RitualEventBuilding(int era, int cost, int prestigePoints, EventEffect<RitualEvent> reaction) {
        super(era, cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(Player player, RitualEvent event) {reaction.manifest(player, event);}
}
