package it.polimi.ingsw.am43.model.board;
import it.polimi.ingsw.am43.model.player.*;

import java.util.*;

public class OrderQueue {

    private final Queue<Player> playerOrder;
    private final List<Integer> foodModifiers;


    public OrderQueue(ArrayList<Player> p, ArrayList<Integer> f) throws IllegalArgumentException{
        if(p.size()!=f.size())  throw new IllegalArgumentException("wrong sizes");
        this.playerOrder = new ArrayDeque<>();
        this.foodModifiers = new ArrayList<>(f);
    }

    public int getFoodModifier(int position) throws IllegalArgumentException {
        try {
            return foodModifiers.get(position);
        }catch (IndexOutOfBoundsException e){throw new IllegalArgumentException("index out of bound");}

    }

    public void append(Player player) {
        this.playerOrder.add(player);
        player.alterFood(this.foodModifiers.get(playerOrder.size()-1));
        //to activete timedbuilding
    }

    public Player pop() {
        return this.playerOrder.poll();
    }
}
