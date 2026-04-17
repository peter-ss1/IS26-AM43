package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Global server-side controller.
 * It manages open lobbies and dispatches commands either to itself
 * or to a specific GameController.
 */
public class ServerController {
    private final Map<Integer, GameController> lobbies;
    private final List<VirtualClient> clients;
    private final BlockingQueue<Command> commandQueue;
    private int nextLobbyId;
    private final Map<UUID, GameController> playerControllers;

    public ServerController() {
        this.lobbies = new HashMap<>();
        this.clients = new ArrayList<>();
        this.commandQueue = new LinkedBlockingQueue<>();
        this.playerControllers = new HashMap<>();
        this.nextLobbyId = 1;
        new Thread(this::executor).start();
    }

    public Map<Integer, GameController> getLobbies() {
        return lobbies;
    }

    public List<VirtualClient> getClients() {
        return clients;
    }

    public void addToQueue(ServerCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        commandQueue.offer(command);
    }

    public void addToQueue(GameCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        GameController lobbyController = playerControllers.get(command.getPlayerId());
        if (lobbyController == null) {
            throw new IllegalArgumentException("Player " + command.getPlayerId() + " is not registered in any lobby");
        }

        lobbyController.addToQueue(command);
    }

    public void registerPlayerController(UUID playerId, GameController controller) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player id cannot be null");
        }
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null");
        }
        playerControllers.put(playerId, controller);
    }

    public GameController getControllerByPlayerId(UUID playerId) {
        return playerControllers.get(playerId);
    }

    private void executor() {
        while (true) {
            try {
                Command command = commandQueue.take();
                command.execute(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public void addClient(VirtualClient client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        clients.add(client);
    }

    public void sendLobbies(VirtualClient client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }

        Map<Integer, Integer> availableLobbies = lobbies.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getNumPlayers()
                ));

        client.sendMessage(new Update.AvailableLobbiesUpdate(availableLobbies));
    }

    public int createLobby(String nickname, Color color, int numPlayers) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be null or blank");
        }
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }

        int lobbyId = nextLobbyId++;
        Game game = new Game(numPlayers, nickname, color);
        GameController gameController = new GameController(this, game);
        lobbies.put(lobbyId, gameController);
        return lobbyId;
    }

    public boolean containsLobby(int lobbyId) {
        return lobbies.containsKey(lobbyId);
    }

    public GameController getLobbyController(int lobbyId) {
        return lobbies.get(lobbyId);
    }

    public int getLobbyIdByController(GameController controller) {
        return lobbies.entrySet().stream()
                .filter(entry -> entry.getValue().equals(controller))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Controller is not registered"));
    }

    public void registerLobbyCreator(VirtualClient client, int lobbyId, String nickname) {
        if (!containsLobby(lobbyId)) {
            client.sendMessage(new Error.LobbyNotFoundError(lobbyId));
            return;
        }

        lobbies.get(lobbyId).registerExistingPlayer(client, nickname);
    }

    public void joinLobby(VirtualClient client, int lobbyId) {
        if (!containsLobby(lobbyId)) {
            client.sendMessage(new Error.LobbyNotFoundError(lobbyId));
            return;
        }

        GameController gameController = getLobbyController(lobbyId);
        client.sendMessage(new Update.LobbyJoinedUpdate(
                lobbyId,
                gameController.getNumPlayers(),
                gameController.getCurrentPlayers()
        ));
    }

    public void addPlayerToLobby(VirtualClient client, int lobbyId, String nickname, Color color) {
        if (!containsLobby(lobbyId)) {
            client.sendMessage(new Error.LobbyNotFoundError(lobbyId));
            return;
        }

        lobbies.get(lobbyId).addPlayer(client, nickname, color);
    }

    public void notifyError(VirtualClient client, String message) {
        if (client != null) {
            client.sendMessage(new Error.GenericServerError(message));
        }
    }
}