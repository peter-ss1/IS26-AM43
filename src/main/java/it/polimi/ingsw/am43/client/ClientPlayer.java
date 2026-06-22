package it.polimi.ingsw.am43.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.PlayerStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the client-side local state of a player in the game.
 * Stores player data, resources, tribe cards, and connection status.
 */
public class ClientPlayer implements Serializable {
    private final String nickname;
    private Color color;
    private int food;
    private int prestigePoints;
    private final List<Integer> tribe;
    private PlayerStatus status;

    /**
     * Constructs a new ClientPlayer with a default state for a given nickname and color.
     *
     * @param nickname The unique identification string.
     * @param color    The color selection.
     */
    public ClientPlayer(@JsonProperty("nickname") String nickname, @JsonProperty("color") Color color) {
        this.nickname = nickname;
        this.color = color;
        this.food = 0;
        this.prestigePoints = 0;
        this.tribe = new ArrayList<>();
        this.status = PlayerStatus.ACTIVE;
    }

    /**
     * Reconstitutes a complete ClientPlayer with full state specifications.
     *
     * @param nickname       The unique identification string.
     * @param color          The color selection.
     * @param food           The player's current food resources.
     * @param prestigePoints The player's current prestige points.
     * @param ids            The collection of card IDs in the tribe.
     * @param status         The current activity status of the player.
     */
    public ClientPlayer(String nickname, Color color, int food, int prestigePoints, List<Integer> ids, PlayerStatus status) {
        this.nickname = nickname;
        this.color = color;
        this.food = food;
        this.prestigePoints = prestigePoints;
        this.tribe = ids;
        this.status = status;
    }

    /**
     * Constructs a new ClientPlayer specifying a custom starting status.
     *
     * @param nickname The unique identification string.
     * @param color    The designated token color.
     * @param status   The initial connection/activity status.
     */
    public ClientPlayer(String nickname, Color color, PlayerStatus status) {
        this.nickname = nickname;
        this.color = color;
        this.food = 0;
        this.prestigePoints = 0;
        this.tribe = new ArrayList<>();
        this.status = status;
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

    public void alterFood(int amount) {
        this.food += amount;
    }

    public void alterPrestigePoints(int amount) {
        this.prestigePoints += amount;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public PlayerStatus getStatus() {
        return this.status;
    }
}
