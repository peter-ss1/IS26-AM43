package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Painting event: rewards players whose number of artists reaches the era
 * threshold and penalizes those who fall short.
 */
public class PaintingEvent extends Event {

    /**
     * @param era the era the event belongs to
     * @param id  the unique id of the event
     */
    public PaintingEvent(int era,int id) {
        super(era,id,1);
    }

    /**
     * {@inheritDoc}
     * Players with fewer artists than the era lose 2 prestige points; the others
     * gain prestige points equal to their number of artists times the era. Event
     * buildings are activated afterwards.
     */
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

    /** {@inheritDoc} */
    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }
}
