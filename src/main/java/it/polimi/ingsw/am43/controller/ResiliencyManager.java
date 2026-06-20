package it.polimi.ingsw.am43.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
/*
public class ResiliencyManager {
    private static final Path UUID_FILE = Paths.get("client_id.txt");

    public static UUID getOrCreateUUID() {
        if (Files.exists(UUID_FILE)) {
            try {
                String savedId = Files.readString(UUID_FILE).trim();
                UUID recoveredUUID = UUID.fromString(savedId);

                System.out.println("Identity recovered: " + recoveredUUID);
                return recoveredUUID;

            } catch (IllegalArgumentException e) {
                System.err.println("Id corrupted, generating new one");
            } catch (IOException e) {
                System.err.println("Disc reading error, generating new one");
            }
        }

        UUID newUUID = UUID.randomUUID();
        try {
            Files.writeString(UUID_FILE, newUUID.toString());
            System.out.println("new player, generating identity: " + newUUID);

        } catch (IOException e) {
            System.err.println("error while writing id on the disc");
        }

        return newUUID;
    }
}
*/
public class ResiliencyManager {
    private static final Path CLIENTS_DIR = Paths.get("clients");

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

