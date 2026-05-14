package it.polimi.ingsw.am43.database;

import java.util.List;
/*-----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Classifica (
        id SERIAL PRIMARY KEY,
        nickname VARCHAR(50) NOT NULL,
punteggio INT NOT NULL,
data_partita TIMESTAMP NOT NULL,
num_giocatori INT NOT NULL
);
------------------------------------------------------------------*/
public class TestDatabase {
    public static void main(String[] args) {
        try {

            DatabaseConfig config = new DatabaseConfig();
            config.setUrl("jdbc:mysql://localhost:3306/mesos_db");
            config.setUser("root");
            config.setPassword("ciaocomestai");


            DatabaseManager.initialize(config);

            ClassificaDAO dao = new ClassificaDAO();

            System.out.println("Salvataggio di alcuni risultati di prova (Partite a 3 giocatori)...");
            dao.saveresult("Lorenzo", 50, 3);

            Thread.sleep(1000);
            dao.saveresult("Marco", 80, 3);
            Thread.sleep(1000);
            dao.saveresult("Giulia", 65, 3);


            dao.saveresult("Anna", 100, 4);


            System.out.println("\nClassifica completa per 3 giocatori:");
            List<String> leaderboard = dao.getFullLeaderboard(3);
            for (String row : leaderboard) {
                System.out.println(row);
            }


            int nuovoPunteggio = 70;
            int posizione = dao.getPlayerRank(nuovoPunteggio, 3);
            System.out.println("\nSe faccio " + nuovoPunteggio + " punti in una partita a 3 giocatori, la mia posizione è: " + posizione);

        } catch (Exception e) {
            System.err.println("Errore durante il test:");
            e.printStackTrace();
        }
    }
}