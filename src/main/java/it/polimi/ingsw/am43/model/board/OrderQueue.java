package it.polimi.ingsw.am43.model.board;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.*;

public class OrderQueue {

    private final Queue<Player> playerOrder;
    private final List<Integer> foodModifiers;


    public OrderQueue(ArrayList<Player> players, ArrayList<Integer> foodModifiers) throws IllegalArgumentException{
        if(players.size()!=foodModifiers.size())  throw new IllegalArgumentException("wrong sizes");
        Collections.shuffle(players);
        this.playerOrder = new ArrayDeque<>();
        this.foodModifiers = new ArrayList<>(foodModifiers);
        for(Player p: players)this.playerOrder.offer(p);

    }

    public int getFoodModifier(int position) throws IllegalArgumentException {
        try {
            return foodModifiers.get(position);
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("index out of bound");}

    }

    public void append(Player player) {
        this.playerOrder.offer(player);
        if(player.getFood()+this.foodModifiers.get(playerOrder.size()-1)>=0)
            player.alterFood(this.foodModifiers.get(playerOrder.size()-1));
        else
            player.alterPrestigePoints(this.foodModifiers.get(playerOrder.size()-1));
        player.getTribe().activateTimedBuilding(null, player, null); //to understand better if there are other solutions
    }

    public Player peek(){return this.playerOrder.peek();}

    public Player pop() {
        return this.playerOrder.poll();
    }
}
