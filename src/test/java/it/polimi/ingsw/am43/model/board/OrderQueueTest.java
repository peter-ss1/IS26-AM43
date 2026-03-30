package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderQueueTest {
    private OrderQueue orderQueue;

    @BeforeEach
    void setUp() {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player("1", Color.RED));
        players.add(new Player("2", Color.BLACK));
        ArrayList<Integer> modifiers = new ArrayList<>();
        modifiers.add(1);
        modifiers.add(-1);
        orderQueue = new OrderQueue(players, modifiers);
    }

    @Test
    void shouldGetLastFoodGiven() {
        assertEquals(-1, orderQueue.getLastFoodGiven());
    }

    @Test
    void shouldAppend() {
        orderQueue.pop();
        orderQueue.pop();
        assertTrue(orderQueue.isEmpty());
        Player player1 = new Player("1", Color.RED);
        orderQueue.append(player1);
        Player player2 = new Player("2", Color.BLACK);
        orderQueue.append(player2);
        assertEquals(1, player1.getFood());
        assertEquals(0, player2.getFood());
        assertEquals(-2, player2.getPrestigePoints());
        assertTrue(orderQueue.isFull());
    }

    @Test
    void shouldPopFirstPlayer() {
        orderQueue.pop();
        orderQueue.pop();
        Player player1= new Player("pippo", Color.WHITE);
        Player player2= new Player("titto", Color.RED);
        this.orderQueue.append(player1);
        this.orderQueue.append(player2);
        assertEquals(player1,this.orderQueue.pop());
        assertEquals(player2,this.orderQueue.pop());
    }

    @Test
    void peek(){
        orderQueue.pop();
        orderQueue.pop();
        Player player1= new Player("pippo", Color.WHITE);
        Player player2= new Player("titto", Color.RED);
        this.orderQueue.append(player1);
        assertEquals(player1,this.orderQueue.peek());
        this.orderQueue.append(player2);
        this.orderQueue.pop();
        assertEquals(player2,this.orderQueue.peek());
    }
}