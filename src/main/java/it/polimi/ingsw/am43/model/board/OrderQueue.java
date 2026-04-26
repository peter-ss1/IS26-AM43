package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.*;

public class OrderQueue {

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

    public void append(Player player) {
        this.playerOrder.offer(player);
        if (player.getFood() + this.foodModifiers.get(playerOrder.size() - 1) >= 0)
            player.alterFood(this.foodModifiers.get(playerOrder.size() - 1));
        else
            player.alterPrestigePoints(-2);
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
