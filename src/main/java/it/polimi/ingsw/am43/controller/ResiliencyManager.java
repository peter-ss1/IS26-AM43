package it.polimi.ingsw.am43.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Manages client side recovery of local identity stored configurations.
 * Ensures that if a client application crashes or is restarted, it can restore its unique identifier (UUID)
 * to reconnect to the server and return to own session.
 */
public class ResiliencyManager {
    private static final Path CLIENTS_DIR = Paths.get("clients");

    /**
     * Retrieves an existing unique network identifier for a given client index from disk,
     * or generates and writes a new one if it does not already exist.
     *
     * @param client a numerical index
     * @return a persistent or newly assigned {@link UUID} uniquely mapping to this client instance
     */
    public static UUID getOrCreateUUID(int client) {

        try {
            if (!Files.exists(CLIENTS_DIR)) {
                Files.createDirectories(CLIENTS_DIR);
            }
        } catch (IOException e) {
            System.err.println("Error while creating recovery directory:  " + e.getMessage());
            return UUID.randomUUID();
        }
        Path uuidFile = CLIENTS_DIR.resolve("client_id_" + client + ".txt");

        if (Files.exists(uuidFile)) {
            try {
                String savedId = Files.readString(uuidFile).trim();
                UUID recoveredUUID = UUID.fromString(savedId);
                System.out.println("The previous client configuration was recovered: " + recoveredUUID);
                return recoveredUUID;

            } catch (IllegalArgumentException | IOException e) {
                System.out.println("Error while reading recovery configuration, generating a new configuration");
            }
        }

        UUID newUUID = UUID.randomUUID();
        try {
            Files.writeString(uuidFile, newUUID.toString());
            System.out.println("A new client configuration has been generated:" + newUUID);
        } catch (IOException e) {
            System.err.println("Error while saving recovery configuration: " + e.getMessage());
        }

        return newUUID;
    }
}

