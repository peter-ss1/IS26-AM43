package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.PointsPair;
import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SustenanceEvent extends Event {

    public SustenanceEvent(int era, int id) {
        super(era, id,2);
    }

    @Override
    public void addToRow(Row row) {
        row.addCard(this);
    }

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

    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }
}
