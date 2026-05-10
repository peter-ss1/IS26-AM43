package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.Serializable;
import java.util.*;

public class OrderQueue implements Serializable {

    private final Queue<Player> playerOrder;
    private final List<Integer> foodModifiers;


    public OrderQueue(List<Player> players, List<Integer> foodModifiers) throws IllegalArgumentException {
        if (players.size() != foodModifiers.size()) throw new IllegalArgumentException("wrong sizes");
        Collections.shuffle(players);
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

    public int getLastFoodGiven() {
        return this.foodModifiers.get(playerOrder.size()-1);
    }

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

    public Player peek() {
        return this.playerOrder.peek();
    }

    public Player pop() {
        return this.playerOrder.poll();
    }

    public boolean isEmpty() {
        return this.playerOrder.isEmpty();
    }

    public boolean isFull() {
        return this.playerOrder.size() == this.foodModifiers.size();
    }

    public List<Color> getColorOrder() {
        return this.playerOrder.stream().map(Player::getColor).toList();
    }
}
