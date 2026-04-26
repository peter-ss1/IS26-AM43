package it.polimi.ingsw.am43.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientPlayer implements Serializable {
    private final String nickname;
    private Color color;
    private int food;
    private int prestigePoints;
    private final List<Integer> tribe;

    public ClientPlayer(String nickname, Color color, int food, int prestigePoints, List<Integer> tribe) {
        this.nickname = nickname;
        this.color = color;
        this.food = food;
        this.prestigePoints = prestigePoints;
        this.tribe = tribe;
    }

    public ClientPlayer(@JsonProperty("nickname") String nickname,@JsonProperty("color") Color color) {
        this.nickname = nickname;
        this.color = color;
        this.food = 0;
        this.prestigePoints = 0;
        this.tribe = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public void setPrestigePoints(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    public List<Integer> getTribe() {
        return tribe;
    }

    public void updateTribe(Integer id) {
        this.tribe.add(id);
    }
}
