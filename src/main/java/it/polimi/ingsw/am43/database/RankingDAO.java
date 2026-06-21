package it.polimi.ingsw.am43.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing match results and leaderboard entries.
 */
public class RankingDAO {
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    /**
     * Saves a player's match result into the database.
     *
     * @param nickname   The unique name of the player.
     * @param points     Total points accumulated by the player.
     * @param numPlayers The number of players of the match.
     */
    public void saveResult(String nickname, int points, int numPlayers) {
        String sql = "INSERT INTO Classifica (nickname, punteggio, data_partita, num_giocatori) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nickname);
            stmt.setInt(2, points);
            stmt.setTimestamp(3, this.timestamp);
            stmt.setInt(4, numPlayers);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error while loading the result: " + e.getMessage());
        }
    }

    /**
     * Retrieves the entire leaderboard matching a specific player count, ordered descending.
     *
     * @param numPlayers The player group size filter.
     * @return A sorted list of leaderboard elements.
     */
    public List<RankElement> getFullLeaderboard(int numPlayers) {
        List<RankElement> leaderBoard = new ArrayList<>();

        String sql = "SELECT nickname, punteggio, data_partita FROM Classifica " +
                "WHERE num_giocatori = ? ORDER BY punteggio DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numPlayers);
            ResultSet rs = stmt.executeQuery();

            int rank = 1;
            while (rs.next()) {
                int points = rs.getInt("punteggio");
                if (!leaderBoard.isEmpty() && leaderBoard.getLast().getPoints() > points) rank++;
                RankElement element = new RankElement(
                        rank,
                        rs.getString("nickname"),
                        points,
                        rs.getTimestamp("data_partita")
                );
                leaderBoard.add(element);
            }
        } catch (SQLException e) {
            System.err.println("Error while loading the leaderboard: " + e.getMessage());
        }
        return leaderBoard;
    }

    /**
     * Calculates a player's ranking based on score (grouping ties in the same rank).
     *
     * @param points    The score of the selected player.
     * @param numPlayers The player group size filter.
     * @return The computed rank index position (1-indexed).
     */
    public int getPlayerRank(int points, int numPlayers) {
        int rank = 1;

        String sql = "SELECT COUNT(distinct punteggio) AS conteggio FROM Classifica " +
                "WHERE num_giocatori = ? AND punteggio > ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numPlayers);
            stmt.setInt(2, points);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rank = rs.getInt("conteggio") + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error while loading the rank: " + e.getMessage());
        }
        return rank;
    }
}