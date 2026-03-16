package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.player.Player;

import java.util.ArrayList;

public class RitualEvent extends Event {
    private final int malus;
    private final ArrayList<Player> losers;
    private final ArrayList<Player> winners;

    public RitualEvent(int era, int malus) {
        super(era);
        this.malus = malus;
        this.losers = new ArrayList<Player>();
        this.winners = new ArrayList<Player>();
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
    public void affectPlayers(ArrayList<Player> players) {
        int minStars = players.getFirst().getShamanStars();
        int maxStars = minStars;
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
            } else if (stars == minStars) {
                this.winners.add(p);
            }
        }
        for (Player p : this.losers) {
            p.alterFood(malus);
            p.getTribe().activateEventBuildings(this, p);
        }
        for (Player p : this.winners) {
            p.alterFood(this.getEra()*5);
            p.getTribe().activateEventBuildings(this, p);
        }
    }

    @Override
    public void triggerBuilding(EventBuilding building, Player player) {
        building.reactToEvent(player, this);
    }
}
