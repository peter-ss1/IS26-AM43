package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.ArrayList;
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
        //TODO custom data class
        Map<String, List<Integer>> effects = new HashMap<>();
        for (Player p : players) {
            int amount = p.getTribe().getTribeNumber() - p.getSustenanceDiscount();
            if (amount <= 0) {
                List<Integer> list = new ArrayList<>();
                list.add(0);
                list.add(0);
                effects.put(p.getNickname(), list);
                continue;
            }
            int excess = p.getFood() - amount;
            List<Integer> list = new ArrayList<>();
            if (excess < 0) {
                list.add(-p.getFood());
                list.add(excess * this.getEra());
                p.alterFood(-p.getFood());
                p.alterPrestigePoints(excess * this.getEra());
                effects.put(p.getNickname(), list);
            } else {
                list.add(-amount);
                list.add(0);
                p.alterFood(-amount);
            }
        }
        observer.broadcast(new Update.SustenaceEventEffectUpdate(effects));
        players.forEach(p -> p.getTribe().activateEventBuildings(observer, this, p));
    }

    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }

    @Override
    public String toString() {
        return "Evento Sostentamento (Era " + getEra() + ") - Devi spendere cibo pari alla grandezza della tua tribù (meno gli sconti). Se non puoi, perdi PV.";
    }
}
