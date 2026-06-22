package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.PointsPair;
import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sustenance event: each player must spend food equal to their tribe size (less
 * any discount); players who cannot pay lose prestige points instead.
 */
public class SustenanceEvent extends Event {

    /**
     * @param era the era the event belongs to
     * @param id  the unique id of the event
     */
    public SustenanceEvent(int era, int id) {
        super(era, id,2);
    }

    /** {@inheritDoc} Adds this event to the given row. */
    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

    /**
     * {@inheritDoc}
     * Each player pays food equal to their tribe size minus the sustenance
     * discount; any shortfall is converted into a prestige point loss (scaled by
     * the era). Event buildings are activated afterwards.
     */
    @Override
    public void affectPlayers(GameObserver observer, List<Player> players) {
        Map<String, PointsPair> effects = new HashMap<>();
        for (Player p : players) {
            int amount = p.getTribe().getTribeNumber() - p.getSustenanceDiscount();
            if (amount <= 0) {
                effects.put(p.getNickname(), new PointsPair(0, 0));
                continue;
            }
            int excess = p.getFood() - amount;
            if (excess < 0) {
                int foodLost = -p.getFood();
                p.alterFood(foodLost);
                p.alterPrestigePoints(excess * this.getEra());
                effects.put(p.getNickname(), new PointsPair(foodLost, excess * this.getEra()));
            } else {
                p.alterFood(-amount);
                effects.put(p.getNickname(), new PointsPair(-amount, 0));
            }
        }
        observer.broadcast(new Update.SustenaceEventEffectUpdate(effects));
        players.forEach(p -> p.getTribe().activateEventBuildings(observer, this, p));
    }

    /** {@inheritDoc} */
    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }

}
