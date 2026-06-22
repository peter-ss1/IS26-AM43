package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.Serializable;
import java.util.*;

/**
 * The turn-order queue. Holds the players waiting to place their totem on the
 * offer track and applies the food/prestige modifiers tied to each queue position.
 */
public class OrderQueue implements Serializable {

    private final Queue<Player> playerOrder;
    private final List<Integer> foodModifiers;


    /**
     * Builds the queue, shuffling the players with the given seed and giving each
     * one the increasing starting food bonus.
     *
     * @param players       the players to enqueue
     * @param foodModifiers the food modifier for each queue position
     * @param seed          the seed used to shuffle the players deterministically
     * @throws IllegalArgumentException if players and modifiers have different sizes
     */
    public OrderQueue(List<Player> players, List<Integer> foodModifiers, long seed) throws IllegalArgumentException {
        if (players.size() != foodModifiers.size()) throw new IllegalArgumentException("wrong sizes");
        Collections.shuffle(players, new Random(seed));
        this.playerOrder = new ArrayDeque<>();
        this.foodModifiers = new ArrayList<>(foodModifiers);
        int i = 1;
        int startingFood = 2;
        for (Player p : players) {
            p.alterFood(startingFood);
            i++;
            if (i % 2 == 0) startingFood++;
            this.playerOrder.offer(p);
        }
    }

    /** @return the food modifier of the last occupied position in the queue */
    public int getLastFoodGiven() {
        return this.foodModifiers.get(playerOrder.size()-1);
    }

    /**
     * Appends a player to the back of the queue and applies the modifier of that position:
     * positive/negative food if affordable, otherwise a -2 prestige penalty.
     *
     * @param observer the observer to notify of the modifier applied
     * @param player   the player to enqueue
     */
    public void append(GameObserver observer, Player player) {
        this.playerOrder.offer(player);
        int modifier = this.foodModifiers.get(playerOrder.size()-1);
        if (modifier == 0) return;
        if (player.getFood() + modifier >= 0) {
            player.alterFood(modifier);
            observer.broadcast(new Update.OrderModifierUpdate(player.getNickname(), modifier, false));
        }
        else {
            player.alterPrestigePoints(-2);
            observer.broadcast(new Update.OrderModifierUpdate(player.getNickname(), -2, true));
        }
    }

    /** @return the next player in the queue without removing them, or null if empty */
    public Player peek() {
        return this.playerOrder.peek();
    }

    /** @return the next player in the queue, removing them, or null if empty */
    public Player pop() {
        return this.playerOrder.poll();
    }

    /** @return true if the queue has no players left */
    public boolean isEmpty() {
        return this.playerOrder.isEmpty();
    }

    /**
     * Removes the given player from the queue, wherever they are.
     *
     * @param player the player to remove
     * @return true if the player was in the queue
     */
    public boolean removePlayer(Player player){
        return this.playerOrder.remove(player);
    }

    /** @return the number of players currently in the queue */
    public int size(){
        return this.playerOrder.size();
    }

    /** @return true if the queue holds all the players (full capacity) */
    public boolean isFull() {
        return this.playerOrder.size() == this.foodModifiers.size();
    }

    /** @return the colors of the players in queue order */
    public List<Color> getColorOrder() {
        return this.playerOrder.stream().map(Player::getColor).toList();
    }
}
