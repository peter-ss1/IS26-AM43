package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RitualEvent extends Event {
    private final int malus;
    private final List<Player> losers;
    private final List<Player> winners;

    public RitualEvent(int era, int id, int malus) {
        super(era, id,1);
        this.malus = malus;
        this.losers = new ArrayList<>();
        this.winners = new ArrayList<>();
    }

    public int getMalus() {
        return this.malus;
    }

    public boolean isLoser(Player player) {
        return this.losers.contains(player);
    }

    public boolean isWinner(Player player) {
        return this.winners.contains(player);
    }

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

    @Override
    public void triggerBuilding(GameObserver observer, EventBuilding building, Player player) {
        building.reactToEvent(observer, player, this);
    }
    @Override
    public String toString() {
        return "Evento Rituale (Era " + getEra() + ") - Il giocatore con meno Stelle Sciamano subisce " + malus + " PV. Chi ne ha di più vince PV extra.";
    }
}
