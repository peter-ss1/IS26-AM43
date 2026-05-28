package it.polimi.ingsw.am43.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RankingDAO {

    public void saveresult(String nickname, int points, int numPlayers) {
        String sql = "INSERT INTO Classifica (nickname, punteggio, data_partita, num_giocatori) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); //chiede connessione
             PreparedStatement pstmt = conn.prepareStatement(sql)) { //prepare forma ricevimento

            pstmt.setString(1, nickname); //valori da sostituire a ???
            pstmt.setInt(2, points);
            // Generazione della data lato Java
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setInt(4, numPlayers);

            pstmt.executeUpdate(); //invia comando completo
            System.out.println("Result saved for " + nickname);

        } catch (SQLException e) {
            System.err.println("Error while loading the result: " +  e.getMessage());
        }
    }


    public List<RankElement> getFullLeaderboard(int numPlayers) {
        List<RankElement> leaderBoard = new ArrayList<>();

        String sql = "SELECT nickname, punteggio, data_partita FROM Classifica " +
                "WHERE num_giocatori = ? ORDER BY punteggio DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, numPlayers);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RankElement element = new RankElement(
                        rs.getString("nickname"),
                        rs.getInt("punteggio"),
                        rs.getTimestamp("data_partita")
                );
                leaderBoard.add(element);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderBoard;
    }


    public int getPlayerRank(int punteggio, int numGiocatori) {
        int rank = 1;

        // Contiamo quante partite hanno un punteggio maggiore per quel numero di giocatori
        String sql = "SELECT COUNT(*) AS conteggio FROM Classifica " +
                "WHERE num_giocatori = ? AND punteggio > ?"; //conta quante righe esisotno che hanno steeso
        //num di giocatori e che hanno punteggio maggiore.

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, numGiocatori);
            pstmt.setInt(2, punteggio);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) { //cursore ceh scorre risultati che ha dato db
                // La posizione è il numero di punteggi maggiori + 1
                rank = rs.getInt("conteggio") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rank;
    }
}