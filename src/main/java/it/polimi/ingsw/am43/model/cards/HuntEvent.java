package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.PointsPair;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuntEvent extends Event {

    public HuntEvent(int era, int id) {
        super(era,id,1);
    }

    @Override
    public void affectPlayers(GameObserver observer, List<Player> players) {
        Map<String, PointsPair> effects = new HashMap<>();
        for (Player p : players) {
            int amount = p.getTribe().getNumberByCharacterType(CharacterType.HUNTER);
            p.alterFood(amount);
            p.alterPrestigePoints(amount * this.getEra());
            effects.put(p.getNickname(), new PointsPair(amount, amount*this.getEra()));
        }
        observer.broadcast(new Update.HuntEventEffectUpdate(effects));
        players.forEach(p -> p.getTribe().activateEventBuildings(observer, this, p));
    }

    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }
}
