package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuntEvent extends Event {

    public HuntEvent(int era, int id) {
        super(era,id);
    }

    @Override
    public void affectPlayers(GameObserver observer, List<Player> players) {
        //TODO custom data class
        Map<String, List<Integer>> effects = new HashMap<>();
        for (Player p : players) {
            int amount = p.getTribe().getNumberByCharacterType(CharacterType.HUNTER);
            p.alterFood(amount);
            p.alterPrestigePoints(amount * this.getEra());
            List<Integer> list = new ArrayList<>();
            list.add(amount);
            list.add(amount * this.getEra());
            effects.put(p.getNickname(), list);
        }
        observer.broadcast(new Update.HuntEventEffectUpdate(effects));
        players.forEach(p -> p.getTribe().activateEventBuildings(observer, this, p));
    }

    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }
    @Override
    public String toString() {
        return "Evento Caccia (Era " + getEra() + ") - Ottieni cibo e PV in base al numero dei tuoi Cacciatori attivi.";
    }
}
