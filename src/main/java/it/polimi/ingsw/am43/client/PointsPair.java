package it.polimi.ingsw.am43.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record PointsPair(@JsonProperty("food") int food,@JsonProperty("prestige") int prestige) implements Serializable {
    public int getFood() { return food; }
    public int getPrestige() { return prestige; }
}
