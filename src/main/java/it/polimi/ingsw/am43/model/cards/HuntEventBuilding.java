package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

public class HuntEventBuilding extends EventBuilding {
    public final EventEffect<HuntEvent> reaction;

    public HuntEventBuilding(int era, int cost, int prestigePoints, EventEffect<HuntEvent> reaction) {
        super(era, cost, prestigePoints);
        this.reaction = reaction;
    }

    @Override
    public void reactToEvent(Player player, HuntEvent event) {reaction.manifest(player, event);}
}
