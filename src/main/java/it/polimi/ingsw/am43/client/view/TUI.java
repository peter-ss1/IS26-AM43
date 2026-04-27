package it.polimi.ingsw.am43.client.view;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.am43.client.view.TextFormatting.*;

public class TUI implements UI, Runnable {
    private final ClientController controller;
    private ViewState state;
    private final ClientModel localModel;
    private final Scanner scanner;
    private final Object printLock;


    public TUI(Scanner scanner) {
        this.localModel = new ClientModel(this);
        this.controller = new ClientController(this, this.localModel);
        this.state = ViewState.CONNECTION;
        this.scanner = scanner;
        this.printLock = new Object();
    }

    @Override
    public void run() {
        this.titleScreen();
        this.setUpClient();
        try {
            this.lobbyChoiceStage();
        } catch (RemoteException e) {
            System.err.println("NetworkError: " + e.getMessage());
        }
    }

    public void lobbyChoiceStage() throws RemoteException {
        this.state = ViewState.LOBBY_CHOICE;
        this.controller.refreshLobbies();
        new Thread(() -> {
            try {
                lobbyChoiceInput();
            } catch (RemoteException e) {
                //TODO disconnection logic
            }
        }).start();
    }

    private void lobbyChoiceInput() throws RemoteException {
        while (true) {
            synchronized (this.printLock) {
                System.out.println("\n[1] Create new lobby  --  [2] Join existing lobby");
                System.out.print("> ");
            }
            String input = this.scanner.nextLine().trim();
            if (input.equals("1")) {
                this.state = ViewState.IN_LOBBY;
                String nickname = "";
                Color color = null;
                int numPlayers = 0;

                synchronized (this.printLock) {
                    while (nickname.isEmpty()) {
                        System.out.print("Enter nickname (3-12 alphanumeric): ");
                        input = scanner.nextLine().trim();
                        if (input.matches("^[a-zA-Z0-9]{3,12}$")) {
                            nickname = input;
                        } else {
                            System.out.println("Invalid nickname length or characters.");
                        }
                    }

                    while (color == null) {
                        System.out.print("Choose color " + Arrays.toString(Color.values()) + ": ");
                        input = scanner.nextLine().trim().toUpperCase();
                        try {
                            color = Color.valueOf(input);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Please enter a valid color.");
                        }
                    }

                    while (numPlayers == 0) {
                        System.out.print("Enter number of players [2-5]: ");
                        input = scanner.nextLine().trim();
                        if (input.matches("\\d+")) {
                            int val = Integer.parseInt(input);
                            if (val >= 2 && val <= 5) {
                                numPlayers = val;
                            } else {
                                System.out.println("Number of players must be between 2 and 5.");
                            }
                        } else {
                            System.out.println("Please enter a number.");
                        }
                    }
                }
                this.controller.createLobby(nickname, color, numPlayers);
                return;
            } else if (input.equals("2")) {
                if (this.localModel.getLobbies().isEmpty()) {
                    synchronized (this.printLock) {
                        System.out.println("There are no lobbies to join.");
                        continue;
                    }
                }
                int lobbyId = 0;
                while (lobbyId == 0) {
                    synchronized (this.printLock) {
                        System.out.println("Enter a lobby ID");
                        System.out.print("> ");
                    }
                    input = scanner.nextLine().trim();
                    synchronized (this.printLock) {
                        if (input.matches("\\d+")) {
                            int val = Integer.parseInt(input);
                            if (this.localModel.getLobbies().stream().anyMatch(lobby -> lobby.lobbyId() == val)) {
                                lobbyId = val;
                            } else {
                                System.out.println("Lobby not available.");
                            }
                        } else {
                            System.out.println("Please enter a number.");
                        }
                    }
                }
                this.controller.joinLobby(lobbyId);
                return;
            } else {
                synchronized (this.printLock) {
                    System.out.println("Please choose an action.");
                }
            }
        }
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
    public void showAvailableLobbies() {
        if (this.state != ViewState.LOBBY_CHOICE) return;
        System.out.print("\r\033[K");
        List<LobbyInfo> availableLobbies = this.localModel.getLobbies();
        if (availableLobbies.isEmpty()) {
            System.out.println("There are no available lobbies. Create one!");
        } else {
            System.out.println("┌──────────┬──────────────────┐");
            System.out.println("│" + BG_BLUE + WHITE + BOLD + " LOBBY ID " + RESET + "│" + BG_BLUE + WHITE + BOLD + "     PLAYERS      " + RESET + "│");
            System.out.println("├──────────┼──────────────────┤");
            for (LobbyInfo lobby : availableLobbies) {
                String players = String.format("%d / %d", lobby.currentPlayers(), lobby.numPlayers());
                System.out.printf("│   %-7d│      %-12s│\n", lobby.lobbyId(), players);
            }
            System.out.println("└──────────┴──────────────────┘");
        }
        System.out.print("\n> ");
    }

    @Override
    public void enterLobby() {
        this.state = ViewState.IN_LOBBY;
        this.printLobbyInfo();
        if (this.localModel.getOwnPlayer() == null) {
            this.joinLobbyForm();
        }
    }

    private void printLobbyInfo() {
        synchronized (printLock) {
            LobbyInfo lobby = this.localModel.getOwnLobby();
            String ownName = this.localModel.getOwnPlayer() == null ? "" : this.localModel.getOwnPlayer().getNickname();
            System.out.println("\n┌──────────────────────────────────────────┐");
            System.out.println("│                  LOBBY                   │");
            System.out.printf("│ ID: %-5d | Status: %d/%-18d │\n",
                    lobby.lobbyId(), lobby.currentPlayers(), lobby.numPlayers());
            System.out.println("├──────────────────────┬───────────────────┤");
            System.out.println("│ PLAYER NICKNAME      │ COLOR             │");
            System.out.println("├──────────────────────┼───────────────────┤");

            List<ClientPlayer> allPlayers = this.localModel.getAllPlayers();
            if (allPlayers.isEmpty()) {
                System.out.println("│ (Waiting for players to connect...)      │");
            } else {
                for (ClientPlayer p : allPlayers) {
                    String displayName = p.getNickname();
                    if (displayName.equals(ownName)) {
                        displayName += INFO_CYAN + " (YOU)" + RESET;
                    }
                    System.out.printf("│ %-29s│ %-18s│\n", displayName, p.getColor());
                }
            }
            System.out.println("└──────────────────────┴───────────────────┘\n");
        }
    }

    private void joinLobbyForm() {
        String nickname = "";
        Color color = null;
        String input;
        synchronized (this.printLock) {
            while (nickname.isEmpty()) {
                System.out.print("Enter nickname (3-12 alphanumeric): ");
                input = scanner.nextLine().trim();
                if (!input.matches("^[a-zA-Z0-9]{3,12}$")) {
                    System.out.println("Invalid nickname length or characters.");
                } else if (!this.localModel.isNicknameAvailable(input)) {
                    System.out.println("Nickname already in use.");
                } else {
                    nickname = input;
                }
            }

            while (color == null) {
                System.out.print("Choose color " + this.localModel.getAvailableColors() + ": ");
                input = scanner.nextLine().trim().toUpperCase();
                try {
                    Color chosenColor = Color.valueOf(input);
                    if (this.localModel.isColorAvailable(chosenColor)) {
                        color = chosenColor;
                    } else {
                        System.out.println("Color already in use.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Please enter a valid color.");
                }
            }
        }
        try {
            this.controller.joinGame(nickname, color);
        } catch (RemoteException e) {
            //TODO reconnection
        }
    }

    @Override
    public void showNewPlayer() {
        if (this.state != ViewState.IN_LOBBY) return;
        this.printLobbyInfo();
    }

    @Override
    public void showGameStart() {
        this.state = ViewState.IN_GAME;
        this.printScoreboard(this.localModel.getAllPlayers(), this.localModel.getCurrentPlayerNickname());
        this.printBoard(this.localModel.getTopRowCards(), this.localModel.getBottomRowCards(), this.localModel.getOrderQueue(), this.localModel.getOfferTrack());
        new Thread(this::gameLoopInput).start();
    }

    private void gameLoopInput() {
        while (true) {
            synchronized (this.printLock) {
                System.out.print("\nPlease enter a command [type 'help' to show command list]: ");
            }
            String input = scanner.nextLine().trim();
            synchronized (printLock) {
                if (input.isEmpty()) continue;
                String[] parts = input.split("\\s+");

                switch (parts[0].toLowerCase()) {
                    case "pick" -> handlePickCard(parts);
                    case "place" -> handlePlaceTotem(parts);
                    case "end" -> handleEndTurn(parts);
                    case "show" -> handleShowCommands(parts);
                    case "help" -> printHelp();
                    default -> System.out.println("Unknown command. Type 'help' to show command list.");
                }
            }
        }
    }

    private void printHelp() {
        synchronized (this.printLock) {
            System.out.println("\n" + "=".repeat(20) + " MESOS COMMAND LIST " + "=".repeat(20));

            System.out.println(BUILD_GOLD + "GAMEPLAY COMMANDS:" + RESET);
            printCommand("place <position>", "Place your totem on the specified offer track card.");
            printCommand("pick <row> <position>", "Take the card at the given position from the board. (e.g. pick top 3)");
            printCommand("end", "Finish your current actions and pass the turn.");

            System.out.println("\n" + BUILD_GOLD + "VISUALIZATION COMMANDS:" + RESET);
            printCommand("show board", "Display the main board");
            printCommand("show scoreboard", "Show players points, food, and current turn order.");
            printCommand("show tribe <nickname>", "View the cards collected by you or another player.");

            System.out.println("\n" + BUILD_GOLD + "SYSTEM:" + RESET);
            printCommand("help", "Show this list of commands.");

            System.out.println("=".repeat(60) + "\n");
        }
    }

    private void printCommand(String syntax, String description) {
        System.out.printf("  " + INFO_CYAN + "%-20s" + RESET + " : %s\n", syntax, description);
    }

    private void handlePickCard(String[] parts) {
        synchronized (printLock) {
            if (this.localModel.isValidating()) {
                System.out.println("Last command is still being processed.");
                return;
            }
            if (!this.localModel.isOwnTurn()) {
                System.out.println("Please wait for your turn.");
                return;
            }
            if (this.localModel.getPhase() != GamePhase.ACTION_RESOLUTION) {
                System.out.println("You are not allowed to perform this action in this phase.");
                return;
            }
            if (parts.length < 3 || (!parts[1].equalsIgnoreCase("top") && !parts[1].equalsIgnoreCase("bottom"))) {
                System.out.println("Invalid request. Format is: pick <row> <position>");
                return;
            }
        }
        try {
            this.controller.pickCard(this.localModel.getIdByPos(parts[1], Integer.parseInt(parts[2])));
        } catch (NumberFormatException e) {
            synchronized (this.printLock) {
                System.out.println("Invalid request. Format is: pick <row> <position>");
            }
        }
    }

    private void handlePlaceTotem(String[] parts) {
        synchronized (printLock) {
            if (this.localModel.isValidating()) {
                System.out.println("Last command is still being processed.");
                return;
            }
            if (!this.localModel.isOwnTurn()) {
                System.out.println("Please wait for your turn.");
                return;
            }
            if (this.localModel.getPhase() != GamePhase.OFFER_TRACK_SELECTION) {
                System.out.println("You are not allowed to perform this action in this phase.");
                return;
            }
            if (parts.length < 2) {
                System.out.println("Invalid request. Format is: place <position>");
                return;
            }
        }
        try {
            this.controller.placeTotem(Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            synchronized (this.printLock) {
                System.out.println("Invalid request. Format is: place <position>");
            }
        }
    }

    private void handleEndTurn(String[] parts) {
        synchronized (printLock) {
            if (this.localModel.isValidating()) {
                System.out.println("Last command is still being processed.");
                return;
            }
            if (!this.localModel.isOwnTurn()) {
                System.out.println("Please wait for your turn.");
                return;
            }
            if (this.localModel.getPhase() != GamePhase.ACTION_RESOLUTION) {
                System.out.println("You are not allowed to perform this action in this phase.");
                return;
            }
        }
        this.controller.endTurn();
    }

    private void handleShowCommands(String[] parts) {
        synchronized (printLock) {
            if (parts.length < 2) {
                System.out.println("Invalid request. Format is: show <board|scoreboard|tribe>");
                return;
            }
        }
        switch (parts[1].toLowerCase()) {
            case "board" ->
                    this.printBoard(this.localModel.getTopRowCards(), this.localModel.getBottomRowCards(), this.localModel.getOrderQueue(), this.localModel.getOfferTrack());
            case "scoreboard" ->
                    this.printScoreboard(this.localModel.getAllPlayers(), this.localModel.getCurrentPlayerNickname());
            case "tribe" -> {
                if (parts.length < 3) {
                    synchronized (this.printLock) {
                        System.out.println("Invalid request. Format is: show tribe <nickname>");
                    }
                } else if (!this.localModel.isPlayer(parts[2])) {
                    synchronized (this.printLock) {
                        System.out.println("Invalid request. Format is: show tribe <nickname>");
                    }
                } else {
                    this.printPlayerTribe(this.localModel.getPlayerByNickname(parts[2]));
                }
            }
            default -> {
                synchronized (this.printLock) {
                    System.out.println("Invalid request. Format is: show <board|scoreboard|tribe>");
                }
            }
        }
    }

    private void printPlayerTribe(ClientPlayer player) {
        synchronized (this.printLock) {
            System.out.println("Player " + player.getNickname() + " has " + player.getFood() + " food and " + player.getPrestigePoints() + " prestige points.");
            printCardsSideBySide(player.getTribe());
        }
    }

    public void printBoard(List<Integer> topRowCards, List<Integer> bottomRowCards, List<Color> orderQueue, List<OfferTrackElement> offerTrack) {
        synchronized (this.printLock) {
            System.out.println("\n" + "=".repeat(20) + " BOARD " + "=".repeat(20));
            System.out.println("\nCURRENT ERA: " + this.localModel.getCurrentEra());
            System.out.println("\n[ TOP ROW ]");
            this.printCardsSideBySide(topRowCards);

            this.printCentralTrack(orderQueue, offerTrack);

            System.out.println("\n[ BOTTOM ROW ]");
            this.printCardsSideBySide(bottomRowCards);

            System.out.println("\n" + "=".repeat(56));
        }
    }

    private void printCentralTrack(List<Color> orderQueue, List<OfferTrackElement> offerTrack) {

        List<String[]> offerAscii = offerTrack.stream()
                .map(OfferTrackElement::prepareCard)
                .toList();
        System.out.println(orderQueue);
        for (int line = 0; line < 9; line++) {
            for (String[] cardLines : offerAscii) {
                System.out.print(cardLines[line] + "  ");
            }
            System.out.println();
        }
    }

    private void printCardsSideBySide(List<Integer> ids) {
        if (ids.isEmpty()) {
            System.out.println("  (Empty)");
            return;
        }
        List<String[]> allAscii = ids.stream()
                .map(id -> this.localModel.getCard(id).getAscii())
                .toList();
        int cardHeight = allAscii.getFirst().length;
        for (int line = 0; line < cardHeight; line++) {
            StringBuilder sb = new StringBuilder();
            for (String[] cardLines : allAscii) {
                sb.append(cardLines[line]).append("  ");
            }
            System.out.println(sb);
        }
    }

    public void printScoreboard(List<ClientPlayer> players, String currentPlayer) {
        synchronized (this.printLock) {
            System.out.println("\n┌───┬──────────────────────┬──────────┬──────────┬──────────┐");
            System.out.println("│ # │ PLAYER               │ COLOR    │ FOOD     │ PRESTIGE │");
            System.out.println("├───┼──────────────────────┼──────────┼──────────┼──────────┤");

            for (int i = 0; i < this.localModel.getNumPlayers(); i++) {
                ClientPlayer p = players.get(i);
                String turnMarker = p.getNickname().equals(currentPlayer) ? BUILD_GOLD + "»" + RESET : " ";
                System.out.printf("│ %s │ %-31s│ %-9s│ %-9s│ %-9s│\n",
                        turnMarker,
                        p.getColor().getAnsiCode() + p.getNickname() + RESET,
                        p.getColor().name(),
                        p.getFood(),
                        p.getPrestigePoints()
                );
            }
            System.out.println("└───┴──────────────────────┴──────────┴──────────┴──────────┘");
        }
    }

    @Override
    public void handleLobbyChoiceError(String message, boolean creation) {
        synchronized (this.printLock) {
            System.out.println("The lobby could not be " + (creation ? "created" : "joined") + " due to: " + message);
        }
        try {
            this.lobbyChoiceStage();
        } catch (RemoteException e) {
            //TODO reconnection
        }
    }

    @Override
    public void handleLobbyJoinError(String message) {
        synchronized (this.printLock) {
            System.out.println("The lobby could not be joined due to: " + message);
        }
        this.joinLobbyForm();
    }

    @Override
    public void showError(String message) {
        synchronized (this.printLock) {
            System.out.println(ERROR + message + RESET);
            this.localModel.stopValidation();
        }
    }

    @Override
    public void showNewCurrPlayer() {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.isOwnTurn()) {
                System.out.println("It's now your turn!");
                this.printBoard(this.localModel.getTopRowCards(), this.localModel.getBottomRowCards(), this.localModel.getOrderQueue(), this.localModel.getOfferTrack());
            } else {
                System.out.println("Current player is now " + this.localModel.getCurrentPlayerNickname());
            }
            System.out.print("Please enter a command [type 'help' to show command list]: ");
        }
    }

    @Override
    public void showTotemPlaced(String nickname, int position) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.isOwnTurn()) {
                System.out.println("You placed the totem in position " + position);
            } else {
                System.out.println(nickname + "'s totem placed in position " + position);
            }
            System.out.print("Please enter a command [type 'help' to show command list]: ");
        }
    }

    @Override
    public void showCardPicked(String nickname, int cardId) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.isOwnTurn()) {
                System.out.println("You picked a card.");
            } else {
                System.out.println(nickname + "picked a card " + cardId);
            }
            System.out.print("Please enter a command [type 'help' to show command list]: ");
        }
    }
}
