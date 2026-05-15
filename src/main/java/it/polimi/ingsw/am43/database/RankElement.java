package it.polimi.ingsw.am43.database;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Timestamp;

public class RankElement implements Serializable {
    @JsonProperty("nickname")
    private final String nickname;
    @JsonProperty("points")
    private final int points;
    @JsonProperty("timestamp")
    private final Timestamp timestamp;

    public RankElement(@JsonProperty("nickname") String nickname,@JsonProperty("points") int points,@JsonProperty("timestamp") Timestamp timestamp) {
        this.nickname = nickname;
        this.points = points;
        this.timestamp = timestamp;
    }

    public String getNickname() {
        return nickname;
    }
    public int getPoints() {
        return points;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
}
