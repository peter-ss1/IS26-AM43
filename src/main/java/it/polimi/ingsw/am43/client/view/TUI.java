package it.polimi.ingsw.am43.client.view;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.enums.Color;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class TUI implements UI, Runnable {
    private final ClientController controller;
    private ViewState state;
    private final ClientModel localModel;
    private boolean initialScene = true;
    private final Scanner scanner;
    private boolean running;


    public TUI(Scanner scanner) {
        this.localModel = new ClientModel(this);
        this.controller = new ClientController(this, this.localModel);
        this.state = ViewState.CONNECTION;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        /*System.out.println(
                "┌─────────────────┐\n" +
                        "│\u001B[48;5;160m\u001B[37m\u001B[1m HUNTING         \u001B[0m│\n" +
                        "│                 │\n" +
                        "│ \u001B[1m Saber-Tooth    \u001B[0m│\n" +
                        "│  Value: 12      │\n" +
                        "│                 │\n" +
                        "│ \u001B[2m         ERA II \u001B[0m│\n" +
                        "└─────────────────┘"
        );  */
        this.titleScreen();
        this.setUpClient();
    }

    private void handleInput(String line) {
    }

    public void titleScreen() {
        System.out.println(" ██████   ██████ ██████████  █████████     ███████     █████████ \n" +
                "░░██████ ██████ ░░███░░░░░█ ███░░░░░███  ███░░░░░███  ███░░░░░███\n" +
                " ░███░█████░███  ░███  █ ░ ░███    ░░░  ███     ░░███░███    ░░░ \n" +
                " ░███░░███ ░███  ░██████   ░░█████████ ░███      ░███░░█████████ \n" +
                " ░███ ░░░  ░███  ░███░░█    ░░░░░░░░███░███      ░███ ░░░░░░░░███\n" +
                " ░███      ░███  ░███ ░   █ ███    ░███░░███     ███  ███    ░███\n" +
                " █████     █████ ██████████░░█████████  ░░░███████░  ░░█████████ \n" +
                "░░░░░     ░░░░░ ░░░░░░░░░░  ░░░░░░░░░     ░░░░░░░     ░░░░░░░░░  \n" +
                "                                                                 \n" +
                "                                                                 \n" +
                "                                                                 ");
    }

    public void setUpClient() {
        String serverIP;
        while (true) {
            System.out.println("Enter Server IP [press enter for localhost]:");
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                serverIP = "localhost";
                break;
            } else if (input.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                serverIP = input;
                break;
            } else {
                System.out.println("Invalid IP. Please use X.X.X.X or default.");
            }
        }

        while (true) {
            System.out.println("\nChoose Connection Type: [1] RMI -- [2] SOCKET");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1") || choice.equals("2")) {
                try {
                    boolean isRmi = choice.equals("1");
                    controller.chooseConnectionType(serverIP, isRmi);

                    System.out.println("Connected to " + serverIP + " via " + (isRmi ? "RMI." : "SOCKET."));
                    break;
                } catch (IOException | NotBoundException e) {
                    System.out.println("Could not reach server at " + serverIP + ": " + e.getMessage());
                    System.out.println("Try again.");
                }
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
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
                    try {
                        this.controller.createLobby(nickname, color, numPlayers);
                        initialScene = false;
                        break;
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (choice.equals("2")) {
                    System.out.print("Choose LobbyID: ");
                    String input = scanner.nextLine().trim();
                    try {
                        this.controller.joinLobby(Integer.parseInt(input));
                        initialScene = false;
                        break;
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
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
            try {
                this.controller.joinGame(nickname, color);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void showPlayer() {
        System.out.println("You are " + this.localModel.getOwnPlayer().getNickname() + " with color " + this.localModel.getOwnPlayer().getColor().name());
    }

    @Override
    public void showNewPlayer() {
        System.out.println("Players in lobby: ");
        this.localModel.getOtherPlayers().forEach((otherPlayer) -> {
            System.out.println(otherPlayer.getNickname() + ": " + otherPlayer.getColor().name());
        });
    }

    @Override
    public void showStartedGame() {
        this.state = ViewState.IN_GAME;
        System.out.println("Game Started : First player is " + this.localModel.getCurrentPlayerNickname());
        System.out.println("Top row contains " + this.localModel.getTopRowCards());
        System.out.println("Bottom row contains " + this.localModel.getBottomRowCards());
    }
}
