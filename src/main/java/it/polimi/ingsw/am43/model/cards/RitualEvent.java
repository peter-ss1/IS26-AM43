package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Ritual event: the players with the fewest shaman stars suffer a malus, while
 * those with the most gain extra prestige points.
 */
public class RitualEvent extends Event {
    private final int malus;
    private final List<Player> losers;
    private final List<Player> winners;

    /**
     * @param era   the era the event belongs to
     * @param id    the unique id of the event
     * @param malus the prestige point malus applied to the losing players
     */
    public RitualEvent(int era, int id, int malus) {
        super(era, id,1);
        this.malus = malus;
        this.losers = new ArrayList<>();
        this.winners = new ArrayList<>();
    }

    /**
     * @return the prestige point malus applied to the losing players
     */
    public int getMalus() {
        return this.malus;
    }

    /**
     * @param player the player to check
     * @return {@code true} if the player is among the losers of this ritual
     */
    public boolean isLoser(Player player) {
        return this.losers.contains(player);
    }

    /**
     * @param player the player to check
     * @return {@code true} if the player is among the winners of this ritual
     */
    public boolean isWinner(Player player) {
        return this.winners.contains(player);
    }

    /**
     * {@inheritDoc}
     * Determines winners (most shaman stars) and losers (fewest shaman stars),
     * applies the malus and the era-based reward, then activates event buildings.
     */
    @Override
    public void affectPlayers(GameObserver observer, List<Player> players) {
        int minStars = players.getFirst().getShamanStars();
        int maxStars = minStars;
        Map<String, Integer> effects = new HashMap<>();
        for (Player p : players) {
            int stars = p.getShamanStars();
            if (stars < minStars) {
                minStars = stars;
                this.losers.clear();
                this.losers.add(p);
            } else if (stars == minStars) {
                this.losers.add(p);
            }
            if (stars > maxStars) {
                maxStars = stars;
                this.winners.clear();
                this.winners.add(p);
            } else if (stars == maxStars) {
                this.winners.add(p);
            }
        }
        for (Player p : this.losers) {
            p.alterPrestigePoints(malus);
            effects.put(p.getNickname(), malus);
        }
        for (Player p : this.winners) {
            p.alterPrestigePoints(this.getEra() * 5);
            effects.merge(p.getNickname(), this.getEra() * 5, Integer::sum);
        }
        observer.broadcast(new Update.RitualEventEffectUpdate(effects));
        for (Player p : Stream.concat(losers.stream(), winners.stream()).distinct().toList()) {
            p.getTribe().activateEventBuildings(observer, this, p);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }
}
