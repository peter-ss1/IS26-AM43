package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaintingEvent extends Event {

    public PaintingEvent(int era,int id) {
        super(era,id);
    }

    @Override
    public void affectPlayers(GameObserver observer, List<Player> players) {
        Map<String, Integer> effects = new HashMap<>();
        for (Player p : players) {
            int amount = p.getTribe().getNumberByCharacterType(CharacterType.ARTIST);
            if (amount < this.getEra()) {
                p.alterPrestigePoints(-2);
                effects.put(p.getNickname(), -2);
            } else {
                p.alterPrestigePoints(amount * this.getEra());
                effects.put(p.getNickname(), amount * this.getEra());
            }
        }
        observer.broadcast(new Update.PaintingEventEffectUpdate(effects));
        players.forEach(p -> p.getTribe().activateEventBuildings(observer, this, p));
    }

    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }
}
