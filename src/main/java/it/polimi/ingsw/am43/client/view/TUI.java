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
import java.util.*;

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
        try {
            CardVisualizer.loadAscii();
        } catch (IOException e) {
            System.err.println("Unable to load ASCII file. " + e.getMessage());
        }
    }

    @Override
    public void run() {
        this.titleScreen();
        this.setUpClient();
        this.lobbyChoiceStage();
    }

    private void setUpClient() {
        this.printWelcome();
        boolean connected = false;
        while (!connected) {
            String serverIP;
            System.out.println("Enter a Server IP [press enter for localhost]:");
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    serverIP = "localhost";
                    break;
                } else if (input.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                    serverIP = input;
                    break;
                } else {
                    this.printError("Invalid IP. Please use X.X.X.X or default.");
                }
            }

            System.out.println("\nChoose Connection Type: [1] RMI -- [2] SOCKET");
            while (true) {
                System.out.print("> ");
                String choice = scanner.nextLine().trim();

                if (choice.equals("1") || choice.equals("2")) {
                    try {
                        boolean isRmi = choice.equals("1");
                        controller.chooseConnectionType(serverIP, isRmi);

                        System.out.println("Successfully connected to " + serverIP + " via " + (isRmi ? "RMI." : "SOCKET."));
                        connected = true;
                        break;
                    } catch (IOException | NotBoundException e) {
                        this.printError("Could not reach server at " + serverIP + ".");
                        break;
                    }
                } else {
                    this.printError("Invalid choice. Please enter 1 or 2.");
                }
            }
        }
    }

    private void lobbyChoiceStage() {
        this.state = ViewState.LOBBY_CHOICE;
        this.printWelcome();
        this.controller.refreshLobbies();
        new Thread(this::lobbyChoiceInput).start();
    }

    private void lobbyChoiceInput() {
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
                        System.out.print("Enter a nickname [3-12 alphanumeric]: ");
                        input = scanner.nextLine().trim();
                        if (input.matches("^[a-zA-Z0-9]{3,12}$")) {
                            nickname = input;
                        } else {
                            this.printError("Invalid nickname length or characters.");
                        }
                    }

                    while (color == null) {
                        System.out.print("Choose a color " + Arrays.toString(Color.values()) + ": ");
                        input = scanner.nextLine().trim().toUpperCase();
                        try {
                            color = Color.valueOf(input);
                        } catch (IllegalArgumentException e) {
                            this.printError("Please enter a valid color.");
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
                                this.printError("Number of players must be between 2 and 5.");
                            }
                        } else {
                            this.printError("Please enter a number.");
                        }
                    }
                }
                this.controller.createLobby(nickname, color, numPlayers);
                return;
            } else if (input.equals("2")) {
                if (this.localModel.getLobbies().isEmpty()) {
                    synchronized (this.printLock) {
                        this.printError("There are no lobbies to join.");
                        continue;
                    }
                }
                int lobbyId = 0;
                while (lobbyId == 0) {
                    synchronized (this.printLock) {
                        System.out.println("Enter a lobby ID: ");
                        System.out.print("> ");
                    }
                    input = scanner.nextLine().trim();
                    synchronized (this.printLock) {
                        if (input.matches("\\d+")) {
                            int val = Integer.parseInt(input);
                            if (this.localModel.getLobbies().stream().anyMatch(lobby -> lobby.getLobbyId() == val)) {
                                lobbyId = val;
                            } else {
                                this.printError("Lobby not available.");
                            }
                        } else {
                            this.printError("Please enter a number.");
                        }
                    }
                }
                this.controller.joinLobby(lobbyId);
                return;
            } else {
                synchronized (this.printLock) {
                    this.printError("Please choose an action.");
                }
            }
        }
    }

    private void joinLobbyForm() {
        String nickname = "";
        Color color = null;
        String input;
        boolean valid = false;
        while (!valid) {
            while (nickname.isEmpty() || !this.localModel.isNicknameAvailable(nickname)) {
                synchronized (printLock) {
                    System.out.println("Enter nickname (3-12 alphanumeric):");
                    System.out.print("> ");
                }
                input = scanner.nextLine().trim();
                synchronized (printLock) {
                    if (!input.matches("^[a-zA-Z0-9]{3,12}$")) {
                        this.printError("Invalid nickname length or characters.");
                    } else if (!this.localModel.isNicknameAvailable(input)) {
                        this.printError("Nickname already in use.");
                    } else {
                        nickname = input;
                    }
                }
            }

            while (color == null || !this.localModel.isColorAvailable(color)) {
                synchronized (printLock) {
                    System.out.println("Choose color " + this.localModel.getAvailableColors() + ":");
                    System.out.print("> ");
                }
                input = scanner.nextLine().trim().toUpperCase();
                synchronized (printLock) {
                    try {
                        Color chosenColor = Color.valueOf(input);
                        if (this.localModel.isColorAvailable(chosenColor)) {
                            color = chosenColor;
                        } else {
                            this.printError("Color already in use.");
                        }
                    } catch (IllegalArgumentException e) {
                        this.printError("Please enter a valid color.");
                    }
                }
            }
            if (!this.localModel.isNicknameAvailable(nickname))
                this.printError("Nickname already in use.");
            else if (!this.localModel.isColorAvailable(color))
                this.printError("Color already in use.");
            else valid = true;
        }
        this.controller.joinGame(nickname, color);
    }

    private void gameLoopInput() {
        while (true) {
            synchronized (this.printLock) {
                this.printGamePrompt();
            }
            String input = scanner.nextLine().trim();
            synchronized (printLock) {
                if (input.isEmpty()) {
                    System.out.println();
                    continue;
                }
                String[] parts = input.split("\\s+");

                switch (parts[0].toLowerCase()) {
                    case "pick" -> handlePickCard(parts);
                    case "place" -> handlePlaceTotem(parts);
                    case "end" -> handleEndTurn();
                    case "show" -> handleShowCommands(parts);
                    case "help" -> printHelp();
                    default -> this.printError("Unknown command. Type 'help' to show command list.");
                }
            }
        }
    }

    private boolean validateOfferAction() {
        if (this.localModel.isValidating()) {
            this.printError("Last command is still being processed.");
            return true;
        }
        if (!this.localModel.isOwnTurn()) {
            this.printError("Please wait for your turn.");
            return true;
        }
        if (this.localModel.getPhase() != GamePhase.ACTION_RESOLUTION) {
            this.printError("You are not allowed to perform this action in this phase.");
            return true;
        }
        return false;
    }

    private void handlePickCard(String[] parts) {
        if (validateOfferAction()) return;
        if (parts.length < 3 || (!parts[1].equalsIgnoreCase("top") && !parts[1].equalsIgnoreCase("bottom"))) {
            this.printError("Invalid request. Format is: pick <row> <position>");
            return;
        }
        try {
            this.controller.pickCard(this.localModel.getIdByPos(parts[1], Integer.parseInt(parts[2])));
        } catch (NumberFormatException e) {
            this.printError("Invalid request. Format is: pick <row> <position>");
        } catch (IllegalArgumentException e) {
            this.printError("Invalid request. Please enter a valid position.");
        }
    }

    private void handlePlaceTotem(String[] parts) {
        if (this.localModel.isValidating()) {
            this.printError("Last command is still being processed.");
            return;
        }
        if (!this.localModel.isOwnTurn()) {
            this.printError("Please wait for your turn.");
            return;
        }
        if (this.localModel.getPhase() != GamePhase.OFFER_TRACK_SELECTION) {
            this.printError("You are not allowed to perform this action in this phase.");
            return;
        }
        if (parts.length < 2) {
            this.printError("Invalid request. Format is: place <position>");
            return;
        }
        try {
            int value = Integer.parseInt(parts[1]);
            if (value < 0 || value > this.localModel.getOfferTrack().size()) {
                this.printError("Invalid request. Please enter a valid position.");
            }
        } catch (NumberFormatException e) {
            this.printError("Invalid request. Format is: place <position>");
        }
        this.controller.placeTotem(Integer.parseInt(parts[1]));
    }

    private void handleEndTurn() {
        if (validateOfferAction()) return;
        this.controller.endTurn();
    }

    private void handleShowCommands(String[] parts) {
        synchronized (printLock) {
            if (parts.length < 2) {
                this.printError("Invalid request. Format is: show <board|scoreboard|tribe>");
                return;
            }
        }
        switch (parts[1].toLowerCase()) {
            case "board" ->
                    this.printBoard(this.localModel.getTopRowCards(), this.localModel.getBottomRowCards(), this.localModel.getOrderQueue(), this.localModel.getOfferTrack());
            case "scoreboard" ->
                    this.printScoreboard(this.localModel.getAllPlayers(), this.localModel.getCurrentPlayerNickname());
            case "tribe" -> {
                if (parts.length < 3) this.printError("Invalid request. Format is: show tribe <nickname>");
                else if (!this.localModel.isPlayer(parts[2]))
                    this.printError("Invalid request. Format is: show tribe <nickname>");
                else this.printPlayerTribe(this.localModel.getPlayerByNickname(parts[2]));
            }
            default -> this.printError("Invalid request. Format is: show <board|scoreboard|tribe>");
        }
    }

    @Override
    public void showAvailableLobbies() {
        if (this.state != ViewState.LOBBY_CHOICE) return;
        synchronized (printLock) {
            System.out.print("\r\033[K");
            List<LobbyInfo> availableLobbies = this.localModel.getLobbies();
            if (availableLobbies.isEmpty()) {
                System.out.println("There are no available lobbies. Create one!");
            } else {
                System.out.println("┌──────────┬──────────────────┐");
                System.out.println("│" + BG_BLUE + WHITE + BOLD + " LOBBY ID " + RESET + "│" + BG_BLUE + WHITE + BOLD + "     PLAYERS      " + RESET + "│");
                System.out.println("├──────────┼──────────────────┤");
                for (LobbyInfo lobby : availableLobbies) {
                    String players = String.format("%d / %d", lobby.getCurrentPlayers(), lobby.getNumPlayers());
                    System.out.printf("│   %-7d│      %-12s│\n", lobby.getLobbyId(), players);
                }
                System.out.println("└──────────┴──────────────────┘");
            }
            System.out.print("\n> ");
        }
    }

    @Override
    public void enterLobby() {
        this.state = ViewState.IN_LOBBY;
        synchronized (printLock) {
            this.printWelcome();
        }
        if (this.localModel.getOwnPlayer() == null) {
            new Thread(this::joinLobbyForm).start();
        }
    }

    @Override
    public void showNewPlayer() {
        if (this.state != ViewState.IN_LOBBY) return;
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            this.printLobbyInfo();
            if (this.localModel.getOwnPlayer() == null) System.out.print("> ");
        }
    }

    @Override
    public void showGameStart() {
        this.state = ViewState.IN_GAME;
        synchronized (printLock) {
            this.printScoreboard(this.localModel.getAllPlayers(), this.localModel.getCurrentPlayerNickname());
            this.printBoard(this.localModel.getTopRowCards(), this.localModel.getBottomRowCards(), this.localModel.getOrderQueue(), this.localModel.getOfferTrack());
        }
        new Thread(this::gameLoopInput).start();
    }

    @Override
    public void handleLobbyChoiceError(String message, boolean creation) {
        synchronized (this.printLock) {
            System.out.println("The lobby could not be " + (creation ? "created" : "joined") + " due to: " + message);
        }
        this.lobbyChoiceStage();
    }

    @Override
    public void handleLobbyJoinError(String message) {
        synchronized (this.printLock) {
            System.out.println("The lobby could not be joined due to: " + message);
        }
        new Thread(this::joinLobbyForm).start();
    }

    @Override
    public void showGameError(String error) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            System.out.println(ERROR + error + RESET);
            this.localModel.stopValidation();
            this.printGamePrompt();
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
            this.printGamePrompt();
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
            this.printGamePrompt();
        }
    }

    @Override
    public void showCardPicked(String nickname, int cardId) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.isOwnTurn()) {
                System.out.println("You picked a card.");
                this.printBoard(this.localModel.getTopRowCards(), this.localModel.getBottomRowCards(), this.localModel.getOrderQueue(), this.localModel.getOfferTrack());
            } else {
                System.out.println(nickname + "picked a card " + cardId);
            }
            this.printGamePrompt();
        }
    }

    @Override
    public void showBuildingAcquisition(String nickname, int cost) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.isOwnTurn()) {
                System.out.println("You bought a building for " + cost + " food.");
            } else {
                System.out.println(nickname + "bought a building for " + cost + " food.");
            }
            this.printGamePrompt();
        }
    }

    @Override
    public void showHunterEffect(String nickname, int food) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.isOwnTurn()) {
                System.out.println("You picked an active hunter that gave you " + food + " food.");
            } else {
                System.out.println(nickname + "picked an active hunter that gave " + food + " food.");
            }
            this.printGamePrompt();
        }
    }

    @Override
    public void showBuildingEffect(String nickname, int bonus, String resource) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.isOwnTurn()) {
                System.out.println("One of your buildings gave you " + bonus + " " + resource + ".");
            } else {
                System.out.println("One of " + nickname + "'s buildings gave " + bonus + " " + resource + ".");
            }
            this.printGamePrompt();
        }
    }

    @Override
    public void showHuntEvent(Map<String, List<Integer>> effects) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            System.out.println("HUNT EVENT!");
            this.printGamePrompt();
        }
    }

    @Override
    public void showPaintingEvent(Map<String, Integer> effects) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            System.out.println("PAINTING EVENT!");
            this.printGamePrompt();
        }
    }

    @Override
    public void showSustenanceEvent(Map<String, List<Integer>> effects) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            System.out.println("SUSTENANCE EVENT!");
            this.printGamePrompt();
        }
    }

    @Override
    public void showRitualEvent(Map<String, Integer> effects) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            System.out.println("RITUAL EVENT!");
            this.printGamePrompt();
        }
    }

    @Override
    public void showGameEnd() {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.getWinners().contains(this.localModel.getOwnPlayer().getNickname())) {
                System.out.println("YOU WON!");
            } else {
                System.out.println("YOU LOST!");
            }
        }
    }

    @Override
    public void showOrderModifier(String nickname, int modifier, boolean prestige) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.getOwnPlayer().getNickname().equals(nickname)) {
                System.out.println("You " + (modifier>0? "received " : "lost ") + modifier + " " + (prestige ? "prestige points" : "food") + ".");
            } else {
                System.out.println(nickname + " " + (modifier>0? "received " : "lost ") + modifier + " " + (prestige ? "prestige points" : "food") + ".");
            }
            this.printGamePrompt();
        }
    }

    @Override
    public void showFoodOffer(String nickname) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.isOwnTurn()) {
                System.out.println("You received 3 food thanks to Offer Card.");
            } else {
                System.out.println(nickname + "received 3 food thanks to Offer Card.");
            }
            this.printGamePrompt();
        }
    }

    private void titleScreen() {
        System.out.println("""
                
                
                 ██████   ██████ ██████████  █████████     ███████     █████████\s
                ░░██████ ██████ ░░███░░░░░█ ███░░░░░███  ███░░░░░███  ███░░░░░███
                 ░███░█████░███  ░███  █ ░ ░███    ░░░  ███     ░░███░███    ░░░\s
                 ░███░░███ ░███  ░██████   ░░█████████ ░███      ░███░░█████████\s
                 ░███ ░░░  ░███  ░███░░█    ░░░░░░░░███░███      ░███ ░░░░░░░░███
                 ░███      ░███  ░███ ░   █ ███    ░███░░███     ███  ███    ░███
                 █████     █████ ██████████░░█████████  ░░░███████░  ░░█████████\s
                ░░░░░     ░░░░░ ░░░░░░░░░░  ░░░░░░░░░     ░░░░░░░     ░░░░░░░░░ \s
                                                                                \s
                                                                                \s
                                                                                \s""");
    }

    private void printError(String error) {
        System.out.println(ERROR + error + RESET);
    }

    private void printWelcome() {
        switch (this.state) {
            case CONNECTION -> System.out.println("Welcome to Connect to Server");
            case LOBBY_CHOICE -> System.out.println("Welcome to Lobby CHOICE");
            case IN_LOBBY -> printLobbyInfo();
            case IN_GAME -> System.out.println("Welcome to In Game");
        }
    }

    private void printGamePrompt() {
        System.out.print("Please enter a command [type 'help' to show command list]: ");
    }

    private void printLobbyInfo() {
        LobbyInfo lobby = this.localModel.getOwnLobby();
        String ownName = this.localModel.getOwnPlayer() == null ? "" : this.localModel.getOwnPlayer().getNickname();
        System.out.println("┌──────────────────────────────────────────┐");
        System.out.println("│                  LOBBY                   │");
        System.out.printf("│ ID: %-5d | Status: %d/%-18d │\n",
                lobby.getLobbyId(), lobby.getCurrentPlayers(), lobby.getNumPlayers());
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
            if (lobby.getCurrentPlayers() != lobby.getNumPlayers()) {
                System.out.println("│ (Waiting for players to connect...)      │");
            }
        }
        System.out.println("└──────────────────────┴───────────────────┘");
    }

    private void printHelp() {
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

    private void printCommand(String syntax, String description) {
        System.out.printf("  " + INFO_CYAN + "%-20s" + RESET + " : %s\n", syntax, description);
    }

    private void printPlayerTribe(ClientPlayer player) {
        System.out.println("Player " + player.getNickname() + " has " + player.getFood() + " food and " + player.getPrestigePoints() + " prestige points.");
        printCardsSideBySide(player.getTribe());
    }

    public void printBoard(List<Integer> topRowCards, List<Integer> bottomRowCards, List<Color> orderQueue, List<OfferTrackElement> offerTrack) {
        System.out.println("\n" + "=".repeat(20) + " BOARD " + "=".repeat(20));
        System.out.println("\n[ TOP ROW ]");
        this.printCardsSideBySide(topRowCards);

        this.printCentralTrack(orderQueue, offerTrack);

        System.out.println("\n[ BOTTOM ROW ]");
        this.printCardsSideBySide(bottomRowCards);

        System.out.println("\n" + "=".repeat(56));
    }

    private void printCentralTrack(List<Color> orderQueue, List<OfferTrackElement> offerTrack) {
        List<List<String>> centralCards = new ArrayList<>();
        centralCards.add(CardVisualizer.getEraASCII(this.localModel.getCurrentEra()));
        centralCards.add(CardVisualizer.getOrderQueueASCII(this.localModel.getNumPlayers(), orderQueue));
        offerTrack.forEach(o -> centralCards.add(CardVisualizer.getOfferTrackASCII(o)));
        int cardHeight = centralCards.getFirst().size();
        for (int line = 0; line < cardHeight; line++) {
            StringBuilder sb = new StringBuilder();
            for (List<String> cardLines : centralCards) {
                sb.append(cardLines.get(line)).append("  ");
            }
            System.out.println(sb);
        }
    }

    private void printCardsSideBySide(List<Integer> ids) {
        if (ids.isEmpty()) {
            System.out.println("No cards to display.");
            return;
        }
        List<List<String>> allAscii = ids.stream()
                .map(CardVisualizer::getASCII)
                .toList();
        int cardHeight = allAscii.getFirst().size();
        for (int line = 0; line < cardHeight; line++) {
            StringBuilder sb = new StringBuilder();
            for (List<String> cardLines : allAscii) {
                sb.append(cardLines.get(line)).append("  ");
            }
            System.out.println(sb);
        }
    }

    public void printScoreboard(List<ClientPlayer> players, String currentPlayer) {
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
