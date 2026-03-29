package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderQueueTest {
    private OrderQueue orderQueue;
    @Test
    void getFoodModifier() {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player("1", Color.RED));
        players.add(new Player("2", Color.BLACK));
        ArrayList<Integer> modifiers = new ArrayList<>();
        modifiers.add(1);
        modifiers.add(0);
        orderQueue = new OrderQueue(players, modifiers);
        orderQueue.getFoodModifier(1);
    }

    @Test
    void append() {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player("1",Color.WHITE));
        players.add(new Player("2", Color.CYAN));
        ArrayList<Integer> modifiers = new ArrayList<>();
        modifiers.add(1);
        modifiers.add(0);
        orderQueue = new OrderQueue(players, modifiers);
        orderQueue.pop();
        orderQueue.append(new Player("2", Color.BLACK));
    }

    @Test
    void pop() {
        Player player1= new Player("pippo", Color.WHITE);
        Player player2= new Player("titto", Color.RED);
        this.orderQueue.append(player1);
        this.orderQueue.append(player2);
        assertEquals(player1,this.orderQueue.pop());
        assertEquals(player2,this.orderQueue.pop());
    }
    @Test
    void peek(){
        Player player1= new Player("pippo", Color.WHITE);
        Player player2= new Player("titto", Color.RED);
        this.orderQueue.append(player1);
        assertEquals(player1,this.orderQueue.peek());
        this.orderQueue.append(player2);
        this.orderQueue.pop();
        assertEquals(player2,this.orderQueue.peek());
    }
}