package it.polimi.ingsw.am43.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassificaDAO {

    public void saveresult(String nickname, int punteggio, int numGiocatori) {
        String sql = "INSERT INTO Classifica (nickname, punteggio, data_partita, num_giocatori) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); //chiede connessione
             PreparedStatement pstmt = conn.prepareStatement(sql)) { //prepare forma ricevimento

            pstmt.setString(1, nickname); //valori da sostituire a ???
            pstmt.setInt(2, punteggio);
            // Generazione della data lato Java
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setInt(4, numGiocatori);

            pstmt.executeUpdate(); //invia comando completo
            System.out.println("Risultato salvato per " + nickname);

        } catch (SQLException e) {
            System.err.println("Errore nel salvataggio del risultato:");
            e.printStackTrace();
        }
    }


    public List<String> getFullLeaderboard(int numGiocatori) {
        List<String> record = new ArrayList<>();

        String sql = "SELECT nickname, punteggio, data_partita FROM Classifica " +
                "WHERE num_giocatori = ? ORDER BY punteggio DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, numGiocatori);
            ResultSet rs = pstmt.executeQuery();

            int posizione = 1; //fa db a testo
            while (rs.next()) {
                record.add(posizione + ". " + rs.getString("nickname") + " - " +
                        rs.getInt("punteggio") + " punti (" +
                        rs.getTimestamp("data_partita") + ")");
                posizione++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return record;
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