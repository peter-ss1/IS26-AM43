package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.board.ModelInterface;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

public class PersistencyManager {

    private static final Path SAVE_DIRECTORY = Paths.get("recovery/");
    static {
        try {
            Files.createDirectories(SAVE_DIRECTORY);
        } catch (IOException e) {
            System.err.println("Error while loading/creating recovery directory: " + e.getMessage());
        }
    }

    private PersistencyManager() { }

    public static void saveRecovery(GameRecovery game, String id) {
        Path tempPath = SAVE_DIRECTORY.resolve(id + "_model.dat.tmp");
        Path finalPath = SAVE_DIRECTORY.resolve(id + "_model.dat");
        try {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempPath.toFile()))) {
                out.writeObject(game);
                out.flush();
            } catch (Exception e) {
                System.err.println("Error while saving recovery file: " + e.getMessage());
            }
            Files.move(tempPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Server saved a recovery file to: " + finalPath.toAbsolutePath());
        }catch (Exception e){
            System.out.println("Error while moving recovery file: " + e.getMessage());
        }

    }


    public static void loadSavedStatus(ServerController serverController){
        if (!Files.exists(SAVE_DIRECTORY)) {
            System.out.println("Server did not find previous recovery files");
            return;
        }

        try (Stream<Path> fileStream = Files.list(SAVE_DIRECTORY)) {
            if (fileStream.findAny().isEmpty()) {
                System.out.println("Server did not find previous recovery files");
                return;
            }
        } catch (IOException e) {
            System.err.println("Error while checking recovery directory contents: " + e.getMessage());
            return;
        }

        String fileNameStructure = "^\\d+_model\\.dat$";


        try (Stream<Path> fileStream = Files.list(SAVE_DIRECTORY)) {
            fileStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().matches(fileNameStructure))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        int gameId = Integer.parseInt(fileName.replace("_model.dat", ""));

                        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            GameRecovery gameRecovery = (GameRecovery) in.readObject();

                            serverController.recoverLobby(gameRecovery);
                            System.out.println("Server loaded the recovery file of Lobby #" + gameId);

                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Error while loading recovery file " + fileName + ": " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            System.err.println("Error while scanning recovery directory: " + e.getMessage());
        }
    }

    public static void deleteRecovery(int id) {
        Path path = SAVE_DIRECTORY.resolve(id + "_model.dat");
        try {
            Files.deleteIfExists(path);
            System.out.println("Server deleted a recovery file from: " + path.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error while deleting recovery file: " + e.getMessage());
        }
    }
}