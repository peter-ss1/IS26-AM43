package it.polimi.ingsw.am43.client.view;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.enums.Color;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class TUI implements UI {
    private final ClientController controller;
    private ViewState state;
    private final ClientModel localModel;
    private boolean initialScene = true;
    private final Scanner scanner;

    public TUI(Scanner scanner) {
        this.localModel = new ClientModel(this);
        this.controller = new ClientController(this, this.localModel);
        this.state = ViewState.CONNECTION;
        this.scanner = scanner;
    }

    public void run() throws RemoteException {
        System.out.println("Choose Connection Type: [1] RMI -- [2] SOCKET");
        String connectionChoice = "";
        while (!connectionChoice.equalsIgnoreCase("1") || !connectionChoice.equalsIgnoreCase("2")) {
            System.out.print("> ");
            connectionChoice = scanner.nextLine().trim();
            if (connectionChoice.equals("1") || connectionChoice.equals("2")) {
                try {
                    controller.chooseConnectionType(connectionChoice.equals("1"));
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Invalid choice.");
            }
        }
        this.state = ViewState.LOBBY_CHOICE;
        this.controller.refreshLobbies();
    }


    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void lobbyUpdate() {
        if (this.state == ViewState.LOBBY_CHOICE) {
            System.out.println("Available Lobbies:");
            System.out.println(this.localModel.getLobbies());
        }
        if (initialScene) {
            System.out.println("Choose action: [1] Create Lobby -- [2] Join Lobby");
            String choice = "";
            while (!choice.equalsIgnoreCase("1") || !choice.equalsIgnoreCase("2")) {
                System.out.print("> ");
                choice = scanner.nextLine().trim();
                if (choice.equals("1")) {
                    System.out.print("Choose nickname, color and number of players: ");
                    String input = scanner.nextLine().trim();
                    String[] parts = input.split("\\s+");
                    String nickname = parts[0];
                    Color color = Color.valueOf(parts[1].toUpperCase());
                    int numPlayers = Integer.parseInt(parts[2]);
                    this.controller.createLobby(nickname, color, numPlayers);
                    initialScene = false;
                    break;
                }
                if (choice.equals("2")) {
                    System.out.print("Choose LobbyID: ");
                    String input = scanner.nextLine().trim();
                    this.controller.joinLobby(Integer.parseInt(input));
                    initialScene = false;
                    break;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        }
    }

    @Override
    public void enterLobby() {
        this.state = ViewState.IN_LOBBY;
        System.out.println("WELCOME TO: " + this.localModel.getOwnLobby());
        if (this.localModel.getOwnPlayer() == null) {
            System.out.print("Choose nickname and color: ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");
            String nickname = parts[0];
            Color color = Color.valueOf(parts[1].toUpperCase());
            this.controller.joinGame(nickname, color);
        }
    }

    @Override
    public void showPlayer() {
        System.out.println("You are " + this.localModel.getOwnPlayer().getNickname() + " with color " + this.localModel.getOwnPlayer().getColor().name());
    }

    @Override
    public void showNewPlayer() {
        System.out.println("Players in lobby: ");
        this.localModel.getOtherPlayers().forEach((otherPlayer) -> {System.out.println(otherPlayer.getNickname() + ": " + otherPlayer.getColor().name());});
    }

    @Override
    public void showStartedGame() {
        this.state = ViewState.IN_GAME;
        System.out.println("Game Started : First player is " + this.localModel.getCurrentPlayerNickname());
        System.out.println("Top row contains " + this.localModel.getTopRowCards());
        System.out.println("Bottom row contains " + this.localModel.getBottomRowCards());
    }
}
