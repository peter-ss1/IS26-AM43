package it.polimi.ingsw.am43.database;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * Represents a single entry in the leaderboard list.
 * It stores a player's rank, identity handle string, final points,
 * and a precise match timestamp.
 */
public class RankElement implements Serializable {
    @JsonProperty("rank")
    private final int rank;
    @JsonProperty("nickname")
    private final String nickname;
    @JsonProperty("points")
    private final int points;
    @JsonProperty("timestamp")
    private final Timestamp timestamp;

    public RankElement(@JsonProperty("rank") int rank, @JsonProperty("nickname") String nickname,@JsonProperty("points") int points,@JsonProperty("timestamp") Timestamp timestamp) {
        this.rank = rank;
        this.nickname = nickname;
        this.points = points;
        this.timestamp = timestamp;
    }

    public int getRank() {return rank;}
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
