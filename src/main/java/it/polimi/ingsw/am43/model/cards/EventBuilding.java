package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.*;

public class EventBuilding extends Building {
    private final EventEffect<SustenanceEvent> sustenanceReaction;
    private final EventEffect<HuntEvent> huntReaction;
    private final EventEffect<PaintingEvent> paintingReaction;
    private final EventEffect<RitualEvent> ritualReaction;

    public EventBuilding(int era, int cost, int prestigePoints, EventEffect<SustenanceEvent> sustenanceReaction, EventEffect<HuntEvent> huntReaction, EventEffect<PaintingEvent> paintingReaction, EventEffect<RitualEvent> ritualReaction) {
        super(era, cost, prestigePoints);
        this.sustenanceReaction = sustenanceReaction;
        this.huntReaction = huntReaction;
        this.paintingReaction = paintingReaction;
        this.ritualReaction = ritualReaction;
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
        this.sustenanceReaction.manifest(player, event);
    }

    public void reactToEvent(Player player, HuntEvent event) {
        this.huntReaction.manifest(player, event);
    }

    public void reactToEvent(Player player, PaintingEvent event) {
        this.paintingReaction.manifest(player, event);
    }

    public void reactToEvent(Player player, RitualEvent event) {
        this.ritualReaction.manifest(player, event);
    }
}
