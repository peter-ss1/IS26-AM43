package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.database.RankElement;
import it.polimi.ingsw.am43.database.RankingDAO;
import it.polimi.ingsw.am43.model.board.ModelInterface;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.PlayerStatus;
import it.polimi.ingsw.am43.model.exceptions.GameEndedException;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.exceptions.IllegalPlayerInitializationException;
import it.polimi.ingsw.am43.model.exceptions.OutOfTurnException;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.GameCommandReceiver;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Update;
import it.polimi.ingsw.am43.utils.Executor;
import it.polimi.ingsw.am43.utils.ServerScheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controller managing a single game instance on the server.
 * Implements an asynchronous command execution pattern to handle client requests
 * or internal server requests (such as network-related notifications).
 * Also dispatches observer updates from the model, and manages database interaction after game conclusion.
 */
public class GameController implements GameObserver, GameCommandReceiver {

    private final ServerController serverController;
    private final ModelInterface model;
    private final ConcurrentHashMap<UUID, String> clients;
    private final ConcurrentHashMap<UUID, String> disconnectedClients;
    private final Executor<GameController> executor;
    private final int lobbyId;
    private boolean gameStarted;
    private boolean gameStopped;
    private boolean gameEnded;
    private boolean singlePlayerPause;
    private ScheduledFuture<?> recoveryTimeoutTask;
    private ScheduledFuture<?> singlePlayerTimeoutTask;

    /**
     * Initializes the controller to handle a newly created game model instance.
     *
     * @param serverController the server manager reference
     * @param model            the game model instance assigned to this controller
     * @param lobbyId          unique identifier of the managed lobby
     * @param nickname         identifying name of the lobby owner player
     * @param playerID         identifying network ID of the lobby owner client
     */
    public GameController(ServerController serverController, ModelInterface model, int lobbyId, String nickname, UUID playerID) {
        this.serverController = serverController;
        this.model = model;
        this.model.setObserver(this);
        this.clients = new ConcurrentHashMap<>();
        this.disconnectedClients = new ConcurrentHashMap<>();
        this.clients.put(playerID, nickname);
        this.executor = new Executor<>(this);
        this.lobbyId = lobbyId;
        this.gameStarted = false;
        this.gameStopped = false;
        this.gameEnded = false;
        this.singlePlayerPause = false;
        this.executor.start();
    }

    /**
     * Restores a previously active game controller instance from disk recovery files after server crash.
     * Also starts a timer to automatically restart game after specified time.
     *
     * @param serverController the server manager reference
     * @param model            the restored game model instance
     * @param lobbyId          unique identifier of the restored lobby
     * @param clients          map matching client network IDs to previous player profiles
     */
    public GameController(ServerController serverController, ModelInterface model, int lobbyId, ConcurrentMap<UUID, String> clients) {
        this.serverController = serverController;
        this.model = model;
        this.model.setObserver(this);
        this.disconnectedClients = new ConcurrentHashMap<>(clients);
        this.clients = new ConcurrentHashMap<>(clients);
        this.executor = new Executor<>(this);
        this.lobbyId = lobbyId;
        this.gameStarted = true;
        this.gameStopped = true;
        this.singlePlayerPause = false;
        this.executor.start();
        this.recoveryTimeoutTask = ServerScheduler.scheduler.schedule(
                () -> this.executor.delegate(new GameCommand.RecoveryTimeoutCommand()),
                40, TimeUnit.SECONDS
        );
    }

    /**
     * Accepts client requests and delegates them to the dedicated executor thread.
     * Rejects commands with errors if the game is paused or has concluded.
     *
     * @param command incoming client requested task
     */
    public void receiveCommand(GameCommand command) {
        if (this.gameStopped || this.gameEnded) {
            this.serverController.sendMessage(command.getPlayerId(), new Error.IllegalMoveError("cannot perform action: game stopped"));
            return;
        }
        this.executor.delegate(command);
    }

    /**
     * Flags a specified player network connection loss to the command executor queue.
     *
     * @param id network UUID of the client that lost connection
     */
    public void notifyDisconnection(UUID id) {
        this.executor.delegate(new GameCommand.DisconnectionCommand(id));
    }

    /**
     * Flags a specified player network connection attempt to the command executor queue.
     *
     * @param id network UUID of the connecting client
     * @throws GameEndedException if the match has already concluded
     */
    public void notifyConnection(UUID id) throws GameEndedException {
        if (this.gameEnded) throw new GameEndedException("");
        else this.executor.delegate(new GameCommand.ConnectionCommand(id));
    }

    /**
     * Flags a specified player lobby join attempt to the command executor queue.
     *
     * @param playerID network UUID of the joining client
     */
    public void joinLobby(UUID playerID) {
        this.executor.delegate(new GameCommand.JoinLobbyCommand(playerID));
    }

    /**
     * Flags a specified player lobby rejoin attempt after disconnection to the command executor queue.
     *
     * @param playerId network UUID of the rejoining client
     */
    public void rejoinLobby(UUID playerId) throws GameEndedException {
        if (this.gameEnded) throw new GameEndedException("");
        else this.executor.delegate(new GameCommand.RejoinLobbyCommand(playerId));
    }

    /**
     * Enforces a temporary pause status with a countdown if only the game contains a single active player.
     *
     * @param name nickname of the remaining player
     */
    public void notifySinglePlayerGame(String name) {
        if (this.singlePlayerPause) return;
        this.gameStopped = true;
        this.singlePlayerPause = true;
        this.singlePlayerTimeoutTask = ServerScheduler.scheduler.schedule(
                () -> this.executor.delegate(new GameCommand.SinglePlayerTimeoutCommand()),
                20, TimeUnit.SECONDS
        );
        this.innerUpdatePlayer(name, new Update.TimerStartedUpdate());
    }

    /**
     * Flags the conclusion of the game to the command executor queue.
     */
    public void notifyEndGame() {
        this.gameEnded = true;
        this.executor.delegate(new GameCommand.EndGameCommand());
    }

    /**
     * @return capacity size of the managed lobby
     */
    public int getNumPlayers() {
        return this.model.getNumPlayers();
    }

    /**
     * @return raw set containing all active and inactive client network identifiers
     */
    public Set<UUID> getPlayers() {
        return this.clients.keySet();
    }

    /**
     * @return count of total connections associated with the lobby
     */
    public int getCurrentPlayers() {
        return this.clients.size();
    }

    /**
     * @return managed lobby unique identifier
     */
    public int getLobbyId() {
        return this.lobbyId;
    }

    /**
     * Callback method processing disconnections. If pre-game, the player is dropped.
     * If mid-game, updates internal variables and flags the player to the model.
     *
     * @param id network UUID of the disconnected client
     */
    public void handleDisconnection(UUID id) {
        if (!this.gameStarted) {
            String name = this.clients.remove(id);
            if (!name.equals("-"))
                this.model.removePlayer(name);
            if (this.clients.isEmpty())
                this.serverController.notifyEndGame(this.lobbyId);
            this.serverController.putPlayerChoosing(id);
            this.innerBroadcast(new Update.PlayerDisconnectedUpdate(name));

        } else {
            String name = this.clients.get(id);
            this.disconnectedClients.put(id, name);
            this.innerBroadcast(new Update.PlayerDisconnectedUpdate(name));
            this.model.moveToInactive(this.model.getPlayerByName(name));
            System.out.println(name + " disconnected from lobby " + this.lobbyId);
        }
    }

    /**
     * Checks if a connecting UUID corresponds to a disconnected client and sends a rejoin prompt.
     *
     * @param id network UUID of the connecting client
     */
    public void handleConnection(UUID id) {
        if (this.disconnectedClients.containsKey(id)) {
            String name = this.disconnectedClients.get(id);
            this.serverController.sendMessage(id, new Update.RejoinRequestUpdate(
                    name, this.model.getPlayerByName(name).getColor()
            ));
        }
    }

    /**
     * Registers a slot booking step inside the lobby pre-game stage.
     *
     * @param playerID network UUID of the joining client
     */
    public void handleJoinLobby(UUID playerID) {
        if (this.clients.isEmpty()) {
            this.serverController.sendMessage(playerID, new Error.LobbyJoinError("lobby closed, please select another lobby."));
            return;
        }
        this.clients.put(playerID, "-");
        this.serverController.sendMessage(playerID, new Update.LobbyJoinedUpdate(
                new LobbyInfo(this.lobbyId, this.getNumPlayers(), this.getCurrentPlayers()),
                this.getPlayersInfo()
        ));
        this.innerBroadcast(new Update.NewLobbyJoinUpdate(this.getCurrentPlayers()));
    }

    /**
     * Processes previous client state and restores active status across internal variables and model.
     * Resumes the game if the lobby is fully populated again.
     *
     * @param playerId network UUID of the rejoining client
     */
    public void handleRejoinLobby(UUID playerId) {
        if (!this.disconnectedClients.containsKey(playerId)) {
            this.serverController.sendMessage(playerId, new Error.GenericServerError("player already connected"));
            return;
        }
        if (this.singlePlayerTimeoutTask != null) this.singlePlayerTimeoutTask.cancel(false);

        this.disconnectedClients.remove(playerId);
        this.serverController.sendMessage(playerId, new Update.LobbyJoinedUpdate(
                new LobbyInfo(this.lobbyId, this.getNumPlayers(), this.getCurrentPlayers()),
                this.getPlayersInfo()
        ));
        String name = this.clients.get(playerId);
        this.innerBroadcast(new Update.PlayerReconnectionUpdate(name));
        System.out.println(name + " reconnected to lobby " + this.lobbyId);
        if (this.singlePlayerPause && this.gameStopped) {
            this.gameStopped = false;
            this.singlePlayerPause = false;
        }
        if (this.model.getPlayerByName(name).getStatus().equals(PlayerStatus.INACTIVE)) {
            this.model.moveToWait(this.model.getPlayerByName(name));
        }
        if (this.disconnectedClients.isEmpty() && this.gameStarted && this.gameStopped) {
            System.out.println("Every player reconnected, lobby " + this.lobbyId + " restarting.");
            if (this.recoveryTimeoutTask != null) this.recoveryTimeoutTask.cancel(false);
            this.gameStopped = false;
            this.model.restartGame();
        }
    }

    /**
     * Resumes the game and marks as disconnected unresponsive clients if the recovery reconnection countdown expires.
     */
    public void handleRecoveryTimeout() {
        if (!this.gameStopped) return;
        if(this.disconnectedClients.size()==this.clients.size()){
            this.endGame();
            return;
        };
        System.out.println("Timeout finished, lobby " + this.lobbyId + " restarting.");
        this.gameStopped = false;
        this.model.restartGame();
        for (UUID id : this.disconnectedClients.keySet()) {
            this.handleDisconnection(id);
        }
    }

    /**
     * Forcefully ends the match if the remaining single active player countdown expires.
     */
    public void handleSinglePlayerTimeout() {
        if (this.gameStopped && this.singlePlayerPause) {
            this.gameEnded = true;
            this.executor.delegate(new GameCommand.EndGameCommand());
        }
    }

    /**
     * Process a new player profile creation inside the model.
     * Initializes the game if all player profiles are completely filled.
     *
     * @param playerID the network UUID of the submitting client
     * @param nickname the client's chosen name
     * @param color    the client's chosen color
     */
    public void joinGame(UUID playerID, String nickname, Color color) {
        if (!this.clients.get(playerID).equals("-")) {
            this.serverController.sendMessage(playerID, new Error.GenericServerError("Player already in game"));
            return;
        }
        try {
            this.model.addPlayer(nickname, color);
            this.clients.replace(playerID, "-", nickname);
            this.serverController.sendMessage(playerID, new Update.GameJoinedUpdate(nickname, color));
            this.broadcast(new Update.PlayerAddedUpdate(nickname, color));
            if (this.clients.values().stream().noneMatch(n -> n.equals("-")) && this.getCurrentPlayers() == this.getNumPlayers()) {
                this.model.startGame();
                this.gameStarted = true;
                PersistencyManager.saveRecovery(new GameRecovery(this.lobbyId, this.model, this.clients), Integer.toString(this.lobbyId));
            }
        } catch (IllegalMoveException e) {
            this.serverController.sendMessage(playerID, new Error.GenericServerError(e.getMessage()));
        } catch (IllegalStateException e) {
            this.serverController.sendMessage(playerID, new Error.WrongPhaseError(e.getMessage()));
        } catch (IllegalPlayerInitializationException e) {
            this.serverController.sendMessage(playerID, new Error.InvalidPlayerError(e.getMessage()));
        }
    }

    /**
     * Processes and dispatches the client's card picking request, notifying possible errors.
     * Saves a backup recovery point beforehand.
     *
     * @param id       target card identifier
     * @param playerID network UUID of the involved player
     */
    public void pickCard(int id, UUID playerID) {
        PersistencyManager.saveRecovery(new GameRecovery(this.lobbyId, this.model, this.clients), Integer.toString(this.lobbyId));
        try {
            this.model.pickCard(this.model.getCardById(id), this.model.getPlayerByName(this.clients.get(playerID)));
        } catch (IllegalMoveException e) {
            this.serverController.sendMessage(playerID, new Error.IllegalMoveError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            this.serverController.sendMessage(playerID, new Error.GenericServerError(e.getMessage()));
        } catch (IllegalStateException e) {
            this.serverController.sendMessage(playerID, new Error.WrongPhaseError(e.getMessage()));
        } catch (OutOfTurnException e) {
            this.serverController.sendMessage(playerID, new Error.OutOfTurnError(e.getMessage()));
        }
    }

    /**
     * Processes and dispatches the client's totem placing request, notifying possible errors.
     * Saves a backup recovery point beforehand.
     *
     * @param position zero-based offer track card index
     * @param playerID network UUID of the involved player
     */
    public void placeTotem(int position, UUID playerID) {
        PersistencyManager.saveRecovery(new GameRecovery(this.lobbyId, this.model, this.clients), Integer.toString(this.lobbyId));
        try {
            this.model.placeTotemOnTrack(this.model.getPlayerByName(this.clients.get(playerID)), position);
        } catch (OutOfTurnException e) {
            this.serverController.sendMessage(playerID, new Error.OutOfTurnError(e.getMessage()));
        } catch (IllegalStateException e) {
            this.serverController.sendMessage(playerID, new Error.WrongPhaseError(e.getMessage()));
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            this.serverController.sendMessage(playerID, new Error.GenericServerError(e.getMessage()));
        } catch (IllegalMoveException e) {
            this.serverController.sendMessage(playerID, new Error.IllegalMoveError(e.getMessage()));
        }
    }

    /**
     * Processes and dispatches the client's turn ending request, notifying possible errors.
     * Saves a backup recovery point beforehand.
     *
     * @param playerID network UUID of the involved player
     */
    public void endTurn(UUID playerID) {
        PersistencyManager.saveRecovery(new GameRecovery(this.lobbyId, this.model, this.clients), Integer.toString(this.lobbyId));
        try {
            this.model.endCurrentTurn(this.model.getPlayerByName(this.clients.get(playerID)));
        } catch (OutOfTurnException e) {
            this.serverController.sendMessage(playerID, new Error.OutOfTurnError(e.getMessage()));
        } catch (IllegalStateException e) {
            this.serverController.sendMessage(playerID, new Error.WrongPhaseError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            this.serverController.sendMessage(playerID, new Error.GenericServerError(e.getMessage()));
        } catch (IllegalMoveException e) {
            this.serverController.sendMessage(playerID, new Error.IllegalMoveError(e.getMessage()));
        }
    }

    /**
     * Concludes the match, commits match outcomes to database records via DAO APIs,
     * broadcasts final rank distributions, and removes disk recovery states.
     */
    public void endGame() {
        RankingDAO dao = new RankingDAO();

        int numPlayers = this.model.getNumPlayers();
        for (Player p : this.model.getAllPlayers().stream()
                .filter(p -> p.getStatus() != PlayerStatus.INACTIVE).toList()) {
            dao.saveResult(p.getNickname(), p.getPrestigePoints(), numPlayers);
        }
        System.out.println("Classification successfully saved to database");
        List<RankElement> fullLeaderboard = dao.getFullLeaderboard(numPlayers);
        Map<String, Integer> playerRanks = new HashMap<>();
        for (Player p : this.model.getAllPlayers().stream()
                .filter(p -> p.getStatus() != PlayerStatus.INACTIVE).toList()) {
            playerRanks.put(p.getNickname(), dao.getPlayerRank(p.getPrestigePoints(), numPlayers));
        }

        this.innerBroadcast(new Update.LeaderboardUpdate(fullLeaderboard, playerRanks));

        this.gameStopped = true;
        this.serverController.notifyEndGame(this.lobbyId);
        PersistencyManager.deleteRecovery(this.lobbyId);
    }

    /**
     * Forwards via network an update notified by the model to every active connected client.
     * Evaluates pause flags before dispatching.
     *
     * @param update the data object to dispatch
     */
    public void broadcast(Update update) {
        if (this.gameStopped) return;
        for (UUID id : this.clients.keySet()) {
            if (!this.disconnectedClients.containsKey(id))
                this.serverController.sendMessage(id, update);
        }
    }

    /**
     * Delivers via network an isolated update notified by the model to one specified client.
     * Evaluates pause flags before dispatching.
     *
     * @param name   the player name
     * @param update the data object to dispatch
     */
    public void updatePlayer(String name, Update update) {
        if (this.gameStopped) return;
        this.clients.entrySet().stream()
                .filter(e -> e.getValue().equals(name))
                .filter(e->!this.disconnectedClients.containsKey(e.getKey()))
                .findFirst()
                .ifPresent(e -> this.serverController.sendMessage(e.getKey(), update));
    }

    /**
     * Dispatches via network an unrestricted broadcast update ignoring any active pause or stop flags.
     *
     * @param update the data object to dispatch
     */
    private void innerBroadcast(Update update) {
        for (UUID id : this.clients.keySet()) {
            if (!this.disconnectedClients.containsKey(id))
                this.serverController.sendMessage(id, update);
        }
    }

    /**
     * Delivers via network an isolated update notified by the model to one specified client,
     * ignoring any active pause or stop flags.
     *
     * @param name   the player name
     * @param update the data object to dispatch
     */
    private void innerUpdatePlayer(String name, Update update) {
        this.clients.entrySet().stream()
                .filter(e -> e.getValue().equals(name))
                .filter(e->!this.disconnectedClients.containsKey(e.getKey()))
                .findFirst()
                .ifPresent(e -> this.serverController.sendMessage(e.getKey(), update));
    }

    /**
     * Maps active players data into a lightweight list for transmission over network.
     */
    private List<ClientPlayer> getPlayersInfo() {
        return this.model.getAllPlayers().stream()
                .map(player -> new ClientPlayer(
                        player.getNickname(),
                        player.getColor(),
                        this.disconnectedClients.containsValue(player.getNickname())
                                ? PlayerStatus.INACTIVE : PlayerStatus.ACTIVE
                )).toList();
    }
}