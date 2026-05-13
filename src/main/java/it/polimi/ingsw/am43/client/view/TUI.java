package it.polimi.ingsw.am43.client.view;

import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.enums.PlayerStatus;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static it.polimi.ingsw.am43.client.view.TextFormatting.*;

public class TUI implements UI, Runnable {
    private final ClientController controller;
    private ViewState state;
    private final ClientModel localModel;
    private final Scanner scanner;
    private final BlockingQueue<String> inputQueue;
    private final Object printLock;
    private boolean signal;

    public TUI(Scanner scanner) {
        this.localModel = new ClientModel(this);
        this.controller = new ClientController(this, this.localModel);
        this.state = ViewState.CONNECTION;
        this.scanner = scanner;
        this.inputQueue = new LinkedBlockingQueue<>();
        this.printLock = new Object();
        this.signal = true;
        try {
            CardVisualizer.loadAscii();
        } catch (IOException e) {
            System.err.println("Unable to load ASCII file. " + e.getMessage());
        }
    }

    public String getInput() throws DisconnectedException {
        while (true) {
            if (this.controller.isDisconnected()) throw new DisconnectedException("Connection lost.");
            if (this.signal) throw new GameStartedException("Game started");
            String input;
            try {
                input = this.inputQueue.poll(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                continue;
            }
            if (input != null) return input;
        }
    }

    @Override
    public void run() {
        this.titleScreen();
        this.setUpClient();
    }

    private void startInputLoop() {
        new Thread(() -> {
            try {
                switch (this.state) {
                    case CONNECTION -> this.setUpClient();
                    case LOBBY_CHOICE -> this.lobbyChoiceInput();
                    case IN_LOBBY -> this.lobbyWaitLoop();
                    case IN_LOBBY_CHOICE -> this.joinLobbyForm();
                    case IN_GAME -> this.gameLoopInput();
                    case RECONNECTION -> this.reconnectionLoop();
                    default -> {
                    }
                }
            } catch (DisconnectedException e) {
                this.printReconnectionWaiting();
            } catch (GameStartedException e) {
                this.state = ViewState.IN_GAME;
                this.signal = false;
                synchronized (printLock) {
                    System.out.print("\r\033[K");
                    this.printWelcome();
                    this.startInputLoop();
                }
            }
        }).start();
    }

    private void lobbyWaitLoop() throws DisconnectedException {
        while (true) {
            synchronized (printLock) {
                System.out.println(MESOS + "Type 'rules' to show game rules" + RESET);
                System.out.print("> ");
            }
            String input = this.getInput().trim();
            synchronized (printLock) {
                if (!input.equalsIgnoreCase("rules")) {
                    this.printError("Check your spelling");
                } else {
                    System.out.println(MESOS + "Here's a summary of the game rules: !!!" + RESET);
                }
            }
        }
    }

    private void printReconnectionWaiting() {
        synchronized (this.printLock) {
            int i = 0;
            while (this.controller.isDisconnected()) {
                if (i == 0 || i == 4) {
                    System.out.print("\r\033[K");
                    System.out.print(ERROR + "Attempting to reconnect" + RESET);
                    i = 0;
                } else {
                    System.out.print(ERROR + "." + RESET);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                i++;
            }
            System.out.println("\n" + MESOS + "Connection restored!" + RESET);
        }
    }

    @Override
    public void showRetrievedInfo() {
        this.state = ViewState.RECONNECTION;
        this.startInputLoop();
    }

    @Override
    public void showDisconnectedPlayer(String nickname) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            this.printError("Player " + nickname + " lost connection.");
            if (this.state == ViewState.IN_LOBBY_CHOICE || this.state == ViewState.IN_LOBBY) {
                this.printLobbyInfo();
                System.out.print("> ");
            } else if (this.state == ViewState.IN_GAME) {
                //this.printScoreboard(this.localModel.getAllPlayers(), this.localModel.getCurrentPlayerNickname());
                this.printGamePrompt();
            }
        }
    }

    private void setUpClient() {
        this.printWelcome();
        boolean connected = false;
        while (!connected) {
            String serverIP;
            System.out.println("Enter a Server IP " + DIM + "[press enter for localhost]" + RESET);
            while (true) {
                System.out.print("> ");
                String input = this.scanner.nextLine().trim();

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

            System.out.println("Choose a connection: " + BOX + " 1 " + RESET + " RMI   " + MESOS + "◈" + RESET + "   " + BOX + " 2 " + RESET + " SOCKET");
            while (true) {
                System.out.print("> ");
                String choice = this.scanner.nextLine().trim();

                if (choice.equals("1") || choice.equals("2")) {
                    try {
                        boolean isRmi = choice.equals("1");
                        synchronized (this.printLock) {
                            new Thread(this::printConnectionWaiting).start();
                            controller.chooseConnectionType(serverIP, isRmi);
                            this.signal = false;
                            System.out.print("\r\033[K");
                            System.out.println(MESOS + "Successfully connected to " + serverIP + " via " + (isRmi ? "RMI." : "SOCKET.") + RESET);
                        }
                        connected = true;
                        break;
                    } catch (Exception e) {
                        this.printError("Could not reach server at " + serverIP + ".");
                        break;
                    }
                } else {
                    this.printError("Invalid choice. Please enter 1 or 2.");
                }
            }
        }
        Thread inputLoop = new Thread(() -> {
            while (true) {
                if (this.scanner.hasNextLine()) {
                    this.inputQueue.offer(this.scanner.nextLine());
                }
            }
        });
        inputLoop.setDaemon(true);
        inputLoop.start();
    }

    private void printConnectionWaiting() {
        int i = 0;
        while (this.signal) {
            if (i == 0 || i == 4) {
                System.out.print("\r\033[K");
                System.out.print(ERROR + "Attempting to connect" + RESET);
                i = 0;
            } else {
                System.out.print(ERROR + "." + RESET);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            i++;
        }
    }

    private void lobbyChoiceStage() {
        this.state = ViewState.LOBBY_CHOICE;
        this.printWelcome();
        this.startInputLoop();
    }

    private void reconnectionLoop() throws DisconnectedException {
        synchronized (this.printLock) {
            while (true) {
                System.out.println("You were previously registered as " + CardVisualizer.getASCIIColor(this.localModel.getOwnPlayer().getColor()) + this.localModel.getOwnPlayer().getNickname() + RESET + ". Would you like to get your player back? [y/n]");
                String input = this.getInput().trim();
                if (input.equalsIgnoreCase("y")) {
                    this.controller.answerRejoin(true);
                    break;
                } else if (input.equalsIgnoreCase("n")) {
                    this.controller.answerRejoin(false);
                    break;
                } else {
                    this.printError("Invalid choice. Please enter y or n.");
                }
            }
        }
    }

    private void lobbyChoiceInput() throws DisconnectedException {
        while (true) {
            synchronized (this.printLock) {
                System.out.print("\r\033[K");
                System.out.println(BOX + " 1 " + RESET + " Create a new lobby   " + MESOS + "◈" + RESET + "   " + BOX + " 2 " + RESET + " Join an existing lobby");
                System.out.print("> ");
            }
            String input = this.getInput().trim();
            if (input.equals("1")) {
                this.state = ViewState.IN_LOBBY;
                String nickname = "";
                Color color = null;
                int numPlayers = 0;

                synchronized (this.printLock) {
                    while (nickname.isEmpty()) {
                        System.out.print("Enter a nickname " + DIM + "[3-12 alphanumeric]" + RESET + ": ");
                        input = this.getInput().trim();
                        if (input.matches("^[a-zA-Z0-9]{3,12}$")) {
                            nickname = input;
                        } else {
                            this.printError("Invalid nickname length or characters.");
                        }
                    }

                    while (color == null) {
                        System.out.print("Choose a color between " + CardVisualizer.getColorArrayString(Arrays.stream(Color.values()).toList()) + ": ");
                        input = this.getInput().trim().toUpperCase();
                        try {
                            color = Color.valueOf(input);
                        } catch (IllegalArgumentException e) {
                            this.printError("Please enter a valid color.");
                        }
                    }

                    while (numPlayers == 0) {
                        System.out.print("Enter number of players " + DIM + "[2-5]" + RESET + ": ");
                        input = this.getInput().trim();
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
                        System.out.println("Enter a lobby ID");
                        System.out.print("> ");
                    }
                    input = this.getInput().trim();
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
                    this.printError("Please choose an option.");
                }
            }
        }
    }

    private void joinLobbyForm() throws DisconnectedException {
        String nickname = "";
        Color color = null;
        String input;
        boolean valid = false;
        while (!valid) {
            while (nickname.isEmpty() || !this.localModel.isNicknameAvailable(nickname)) {
                synchronized (printLock) {
                    System.out.println("Enter nickname " + DIM + "[3-12 alphanumeric]" + RESET);
                    System.out.print("> ");
                }
                input = this.getInput().trim();
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
                    System.out.println("Choose a color between " + CardVisualizer.getColorArrayString(this.localModel.getAvailableColors()));
                    System.out.print("> ");
                }
                input = this.getInput().trim().toUpperCase();
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

    private void gameLoopInput() throws DisconnectedException {
        while (true) {
            synchronized (this.printLock) {
                this.printGamePrompt();
            }
            String input = this.getInput().trim();
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
                } //TODO RULES
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
            this.controller.pickCard(this.localModel.getIdByPos(parts[1], Integer.parseInt(parts[2]) - 1));
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
            if (value < 1 || value > this.localModel.getOfferTrack().size()) {
                this.printError("Invalid request. Please enter a valid position.");
                return;
            }
        } catch (NumberFormatException e) {
            this.printError("Invalid request. Format is: place <position>");
            return;
        }
        this.controller.placeTotem(Integer.parseInt(parts[1]) - 1);
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
            this.printAvailableLobbies();
        }
    }

    @Override
    public void enterLobbyChoice() {
        synchronized (printLock) {
            this.lobbyChoiceStage();
            this.printAvailableLobbies();
        }
    }

    @Override
    public void showPlayerReconnection(String nickname) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            System.out.println("Player " + nickname + " reconnected");
            if (this.state == ViewState.IN_LOBBY_CHOICE || this.state == ViewState.IN_LOBBY) {
                this.printLobbyInfo();
                System.out.print("> ");
            } else if (this.state == ViewState.IN_GAME) {
                //this.printScoreboard(this.localModel.getAllPlayers(), this.localModel.getCurrentPlayerNickname());
                this.printGamePrompt();
            }
        }
    }

    @Override
    public void showDisconnection() {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            this.printError("Connection with server lost");
        }
    }

    private void printAvailableLobbies() {
        System.out.print("\r\033[K");
        List<LobbyInfo> availableLobbies = this.localModel.getLobbies();
        if (availableLobbies.isEmpty()) {
            System.out.println(MESOS + "There are no available lobbies. Create one!\n" + RESET);
        } else {
            System.out.println("    ┌──────────┬──────────────────┐");
            System.out.println("    │" + MESOS + BOLD + " LOBBY ID " + RESET + "│" + MESOS + BOLD + "     PLAYERS      " + RESET + "│");
            System.out.println("    ├──────────┼──────────────────┤");
            for (LobbyInfo lobby : availableLobbies) {
                String players = String.format("%d / %d", lobby.getCurrentPlayers(), lobby.getNumPlayers());
                System.out.printf("    │    %-6d│      %-12s│\n", lobby.getLobbyId(), players);
            }
            System.out.println("    └──────────┴──────────────────┘\n");
        }
        System.out.print("> ");
    }

    @Override
    public void enterLobby() {
        this.state = ViewState.IN_LOBBY;
        synchronized (printLock) {
            this.printWelcome();
        }
        if (this.localModel.getOwnPlayer() == null) this.state = ViewState.IN_LOBBY_CHOICE;
        this.startInputLoop();
    }

    @Override
    public void showNewPlayer() {
        if (this.state != ViewState.IN_LOBBY && this.state != ViewState.IN_LOBBY_CHOICE) return;
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            this.printLobbyInfo();
            if (this.localModel.getOwnPlayer() != null && this.state != ViewState.IN_LOBBY) {
                this.state = ViewState.IN_LOBBY;
                this.startInputLoop();
                return;
            }
            System.out.print("> ");
        }
    }

    @Override
    public void showGameStart() {
        this.signal = true;
    }

    @Override
    public void handleLobbyChoiceError(String message, boolean creation) {
        synchronized (this.printLock) {
            this.printError("The lobby could not be " + (creation ? "created" : "joined") + " due to: " + message);
        }
        this.lobbyChoiceStage();
    }

    @Override
    public void handleLobbyJoinError(String message) {
        synchronized (this.printLock) {
            this.printError("The lobby could not be joined due to: " + message);
        }
        this.state = ViewState.IN_LOBBY_CHOICE;
        this.startInputLoop();
    }

    @Override
    public void showGameError(String error) {
        if (this.state != ViewState.IN_GAME) return;
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            this.printError(error);
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
            position++;
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
                this.printCard(cardId);
                this.printBoard(this.localModel.getTopRowCards(), this.localModel.getBottomRowCards(), this.localModel.getOrderQueue(), this.localModel.getOfferTrack());
            } else {
                System.out.println(nickname + "picked a card.");
                this.printCard(cardId);
            }
            this.printGamePrompt();
        }
    }

    @Override
    public void showBuildingAcquisition(String nickname, int cost) {
        synchronized (this.printLock) {
            System.out.print("\r\033[K");
            if (this.localModel.isOwnTurn()) {
                System.out.println("You paid " + cost + " food to buy the building.");
            } else {
                System.out.println(nickname + "paid " + cost + " food to buy the building.");
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
                System.out.println("You " + (modifier > 0 ? "received " : "lost ") + modifier + " " + (prestige ? "prestige points" : "food") + ".");
            } else {
                System.out.println(nickname + " " + (modifier > 0 ? "received " : "lost ") + modifier + " " + (prestige ? "prestige points" : "food") + ".");
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
        System.out.println(MESOS + """
                
                
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
                                                                                \s""" + RESET);
    }

    private void printError(String error) {
        System.out.println(ERROR + error + RESET);
    }

    private void printWelcome() {
        switch (this.state) {
            case CONNECTION ->
                    System.out.println(MESOS + " █▀▀ █▀▀ █▀█ █  █ █▀▀ █▀█   █▀▀ █▀█ █▀█ █▀█ █▀▀ █▀▀ ▀█▀ █ █▀█ █▀█\n" +
                            " ▀▀█ █▀▀ █▀▄ ▀▄▄▀ █▀▀ █▀▄   █   █ █ █ █ █ █ █▀▀ █    █  █ █ █ █ █\n" +
                            " ▀▀▀ ▀▀▀ ▀ ▀  ▀▀  ▀▀▀ ▀ ▀   ▀▀▀ ▀▀▀ ▀ ▀ ▀ ▀ ▀▀▀ ▀▀▀  ▀  ▀ ▀▀▀ ▀ ▀" + RESET);
            case LOBBY_CHOICE ->
                    System.out.println("\n\n" + MESOS + " █    █▀▀█ █▀▀█ █▀▀█ █ █    █▀▀ █  █ █▀▀█ ▀█▀ █▀▀ █▀▀\n" +
                            " █    █  █ █▀▀▄ █▀▀▄ ▀▄▀    █   █▀▀█ █  █  █  █   █▀▀\n" +
                            " █▄▄█ ▀▀▀▀ █▀▀  █▀▀   █     ▀▀▀ ▀  ▀ ▀▀▀▀ ▀▀▀ ▀▀▀ ▀▀▀\n" + RESET);
            case IN_LOBBY -> {
                System.out.println("\n" + MESOS + " WELCOME TO YOUR LOBBY!" + RESET);
                printLobbyInfo();
            }
            case IN_GAME -> {
                System.out.println(MESOS +
                        "                                               ╔══════════════════╗\n" +
                        "═══════════════════════════════════════════════╣ WELCOME TO MESOS ╠═══════════════════════════════════════════════\n" +
                        "                                               ╚══════════════════╝\n" +
                        "Thousands of years ago, a new era was beginning for humankind...\n" +
                        "The nomadic hunter-gatherers who had laboriously earned their place on Earth organized into small\n" +
                        "groups, differentiating social roles, building the first settlements, and initiating a great revolution.\n" +
                        BOLD + "Welcome to the Mesolithic!\n" + RESET + MESOS +
                        "Step into the role of a tribal leader, carefully choose the tasks to entrust to the people joining your tribe,\n" +
                        "construct specialized buildings, and prepare wisely for the events you will face, guiding your tribe to victory!\n" +
                        "══════════════════════════════════════════════════════════════════════════════════════════════════════════════════" + RESET);
                this.printScoreboard(this.localModel.getAllPlayers(), this.localModel.getCurrentPlayerNickname());
                this.printBoard(this.localModel.getTopRowCards(), this.localModel.getBottomRowCards(), this.localModel.getOrderQueue(), this.localModel.getOfferTrack());
            }
        }
    }

    private void printGamePrompt() {
        System.out.print(MESOS + "Please enter a command [type 'help' to show command list]: " + RESET);
    }

    private void printLobbyInfo() {
        LobbyInfo lobby = this.localModel.getOwnLobby();
        String ownName = this.localModel.getOwnPlayer() == null ? "" : this.localModel.getOwnPlayer().getNickname();
        System.out.println(" ┌────────────────────┐");
        System.out.printf(" │ " + MESOS + "LOBBY #%-5d   %d/%d" + RESET + " │\n",
                lobby.getLobbyId(), lobby.getCurrentPlayers(), lobby.getNumPlayers());
        System.out.println(" ├────────────────────┤");
        List<ClientPlayer> allPlayers = this.localModel.getAllPlayers();
        if (allPlayers.isEmpty()) {
            System.out.println(" └────────────────────┘");
        } else {
            for (ClientPlayer p : allPlayers) {
                if (p.getStatus().equals(PlayerStatus.INACTIVE)) {
                    System.out.println(" │   " + DIM + "reconnecting..." + RESET + "  │");
                } else {
                    String displayName = CardVisualizer.getASCIIColor(p.getColor()) + p.getNickname() + RESET;
                    if (p.getNickname().equals(ownName)) {
                        displayName += MESOS + " (YOU)" + RESET;
                    }
                    System.out.println(" │" + CardVisualizer.centerLine(displayName, RESET, 20) + "│");
                }
            }
            for (int i = allPlayers.size(); i < lobby.getCurrentPlayers(); i++) {
                System.out.println(" │     " + DIM + "choosing..." + RESET + "    │");
            }
            System.out.println(" └────────────────────┘");
        }
        if (lobby.getCurrentPlayers() != lobby.getNumPlayers()) {
            System.out.println("Waiting for other players to connect...");
        }
    }

    private void printHelp() {
        System.out.println("\n" + "=".repeat(20) + " MESOS COMMAND LIST " + "=".repeat(20));

        System.out.println(MESOS + "GAMEPLAY COMMANDS:" + RESET);
        printCommand("place <position>", "Place your totem on the specified offer track card.");
        printCommand("pick <row> <position>", "Take the card at the given position from the board. (e.g. pick top 3)");
        printCommand("end", "Finish your current actions and pass the turn.");

        System.out.println("\n" + MESOS + "VISUALIZATION COMMANDS:" + RESET);
        printCommand("show board", "Display the main board");
        printCommand("show scoreboard", "Show players points, food, and current turn order.");
        printCommand("show tribe <nickname>", "View the cards collected by you or another player.");

        System.out.println("=".repeat(60) + "\n");
    }

    private void printCommand(String syntax, String description) {
        System.out.printf("  " + MESOS + "%-25s" + RESET + " : %s\n", syntax, description);
    }

    private void printPlayerTribe(ClientPlayer player) {
        System.out.println(" ╔═════════════════════════╦═════════════╦═════════════════╗");
        System.out.printf(" ║%s║ " + MESOS + "FOOD" + RESET + ": %-6d║ " + MESOS + "PRESTIGE" + RESET + ": %-6d║\n",
                CardVisualizer.centerLine(player.getNickname() + "'s Tribe", CardVisualizer.getASCIIColor(player.getColor()), 25),
                player.getFood(),
                player.getPrestigePoints()
        );
        System.out.println(" ╚═════════════════════════╩═════════════╩═════════════════╝");
        Map<String, List<Integer>> tribeMap = CardVisualizer.divideTribe(player.getTribe());
        if (tribeMap.isEmpty()) {
            System.out.println("This tribe is still empty.");
            return;
        }
        for (List<Integer> l : tribeMap.values()) {
            this.printRow(l);
        }
    }

    public void printBoard(List<Integer> topRowCards, List<Integer> bottomRowCards, List<Color> orderQueue, List<OfferTrackElement> offerTrack) {
        System.out.println(MESOS +
                "                                               ╔═══════════════╗\n" +
                "═══════════════════════════════════════════════╣     BOARD     ╠═══════════════════════════════════════════════\n" +
                "                                               ╚═══════════════╝\n" + RESET);
        int cardWidth = CardVisualizer.getASCII(topRowCards.getFirst()).getFirst().length();
        this.printIndexes(0, topRowCards.size(), cardWidth);
        this.printRow(topRowCards);

        this.printIndexes(2, 2 + offerTrack.size(), cardWidth);
        this.printCentralTrack(orderQueue, offerTrack);

        this.printIndexes(0, bottomRowCards.size(), cardWidth);
        this.printRow(bottomRowCards);

        System.out.println(MESOS + "════════════════════════════════════════════════════════════════════════════════════════════════════════════" + RESET);
    }

    private void printIndexes(int startingPoint, int size, int cardWidth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < startingPoint; i++) {
            sb.append(" ".repeat(cardWidth + 2));
        }
        for (int i = 1; i <= (size - startingPoint); i++) {
            String indexLabel = "[" + i + "]";
            sb.append(CardVisualizer.centerLine(indexLabel, MESOS, cardWidth));
            sb.append("  ");
        }
        System.out.println(sb);
    }

    private void printCentralTrack(List<Color> orderQueue, List<OfferTrackElement> offerTrack) {
        List<List<String>> centralCards = new ArrayList<>();
        centralCards.add(CardVisualizer.getEraASCII(this.localModel.getCurrentEra()));
        centralCards.add(CardVisualizer.getOrderQueueASCII(this.localModel.getNumPlayers(), orderQueue, this.localModel.getInactivePlayers(), this.localModel.getWaitingPlayers()));
        offerTrack.forEach(o -> centralCards.add(CardVisualizer.getOfferTrackASCII(o)));
        printSideBySide(centralCards);
    }

    private void printSideBySide(List<List<String>> cards) {
        int cardHeight = cards.getFirst().size();
        for (int line = 0; line < cardHeight; line++) {
            StringBuilder sb = new StringBuilder();
            for (List<String> cardLines : cards) {
                sb.append(cardLines.get(line)).append("  ");
            }
            System.out.println(sb);
        }
        System.out.println();
    }

    private void printRow(List<Integer> ids) {
        if (ids.isEmpty()) {
            System.out.println("No cards to display.");
            return;
        }
        List<List<String>> allAscii = ids.stream()
                .map(CardVisualizer::getASCII)
                .toList();
        printSideBySide(allAscii);
    }

    public void printScoreboard(List<ClientPlayer> players, String currentPlayer) {
        System.out.println(" ╔═══╦════════════════════╦══════════╦══════════╗");
        System.out.println(" ║ " + MESOS + "#" + RESET + " ║       " + MESOS + "PLAYER" + RESET + "       ║   " + MESOS + "FOOD" + RESET + "   ║ " + MESOS + "PRESTIGE" + RESET + " ║");
        System.out.println(" ╠═══╬════════════════════╬══════════╬══════════╣");
        for (ClientPlayer p : players) {
            String turnMarker = p.getNickname().equals(currentPlayer) ? MESOS + "»" + RESET : " ";
            String name = p.getStatus().equals(PlayerStatus.INACTIVE) ? "reconnecting..." : p.getNickname();
            String color = p.getStatus().equals(PlayerStatus.INACTIVE) ? DIM : CardVisualizer.getASCIIColor(p.getColor());
            System.out.printf(" ║ %s ║%s║    %-6d║    %-6d║\n",
                    turnMarker,
                    CardVisualizer.centerLine(name, color, 20),
                    p.getFood(),
                    p.getPrestigePoints()
            );
        }
        System.out.println(" ╚═══╩════════════════════╩══════════╩══════════╝");
    }

    private void printCard(int cardId) {
        StringBuilder sb = new StringBuilder();
        for (String s : CardVisualizer.getASCII(cardId)) {
            sb.append(s).append("\n");
        }
        System.out.println(sb);
    }
}
