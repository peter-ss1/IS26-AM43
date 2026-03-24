package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderQueueTest {
    private OrderQueue orderQueue;
    @Test
    void getFoodModifier() {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player("1"));
        players.add(new Player("2"));
        ArrayList<Integer> modifiers = new ArrayList<>();
        modifiers.add(1);
        modifiers.add(0);
        orderQueue = new OrderQueue(players, modifiers);
        orderQueue.getFoodModifier(1);
    }

    @Test
    void append() {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player("1"));
        players.add(new Player("2"));
        ArrayList<Integer> modifiers = new ArrayList<>();
        modifiers.add(1);
        modifiers.add(0);
        orderQueue = new OrderQueue(players, modifiers);
        orderQueue.pop();
        orderQueue.append(new Player("2"));
    }

    @Test
    void pop() {
    }
}