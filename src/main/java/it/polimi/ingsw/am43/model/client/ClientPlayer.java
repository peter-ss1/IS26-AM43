package it.polimi.ingsw.am43.model.client;

import it.polimi.ingsw.am43.model.enums.Color;

import java.util.List;

public class ClientPlayer {
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
