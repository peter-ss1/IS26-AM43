package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.PointsPair;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hunt event: rewards each player with food and prestige points based on the
 * number of hunters in their tribe.
 */
public class HuntEvent extends Event {

    /**
     * @param era the era the event belongs to
     * @param id  the unique id of the event
     */
    public HuntEvent(int era, int id) {
        super(era,id,1);
    }

    /**
     * {@inheritDoc}
     * Each player gains food and prestige points (food times the era) equal to
     * their number of hunters, then their event buildings are activated.
     */
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

    /** {@inheritDoc} */
    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }
}
