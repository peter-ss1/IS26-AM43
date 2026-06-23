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
import java.util.concurrent.*;


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
        this.gameEnded=false;
        this.singlePlayerPause=false;
        this.executor.start();
    }

    //recovery
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
        this.singlePlayerPause=false;
        this.executor.start();
        this.recoveryTimeoutTask = ServerScheduler.scheduler.schedule(
                () -> this.executor.delegate(new GameCommand.RecoveryTimeoutCommand()),
                40, TimeUnit.SECONDS
        );
    }

    public void receiveCommand(GameCommand command) {
        if (this.gameStopped || this.gameEnded){
            this.serverController.sendMessage(command.getPlayerId(),new Error.IllegalMoveError("cannot perform action: game stopped"));
            return;
        }
        this.executor.delegate(command);
    }


    public void notifyDisconnection(UUID id) {
        this.executor.delegate(new GameCommand.DisconnectionCommand(id));
    }

    public void notifyConnection(UUID id) throws GameEndedException {
        if (this.gameEnded) throw new GameEndedException("");
        else this.executor.delegate(new GameCommand.ConnectionCommand(id));
    }

    public void joinLobby(UUID playerID) {
        this.executor.delegate(new GameCommand.JoinLobbyCommand(playerID));
    }

    public void rejoinLobby(UUID playerId) throws GameEndedException{
        if (this.gameEnded) throw new GameEndedException("");
        else this.executor.delegate(new GameCommand.RejoinLobbyCommand(playerId));
    }


    public void notifySinglePlayerGame(String name){
        if (this.singlePlayerPause)return;
        this.gameStopped=true;
        this.singlePlayerPause=true;
        this.singlePlayerTimeoutTask= ServerScheduler.scheduler.schedule(
                () -> this.executor.delegate(new GameCommand.SinglePlayerTimeoutCommand()),
                20, TimeUnit.SECONDS
        );
        this.innerUpdatePlayer(name,new Update.TimerStartedUpdate());
    }

    public void notifyEndGame(){
        this.gameEnded=true;
        this.executor.delegate(new GameCommand.EndGameCommand());
    }



    public int getNumPlayers() { return this.model.getNumPlayers(); }
    public Set<UUID> getPlayers() { return this.clients.keySet(); }
    public int getCurrentPlayers() { return this.clients.size(); }
    public int getLobbyId() { return this.lobbyId; }


    public void handleDisconnection(UUID id) {
        if (!this.gameStarted) {
            String name = this.clients.remove(id);
            if(!name.equals("-"))
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

    public void handleConnection(UUID id) {
        if (this.disconnectedClients.containsKey(id)) {
            String name = this.disconnectedClients.get(id);
            this.serverController.sendMessage(id, new Update.RejoinRequestUpdate(
                    name, this.model.getPlayerByName(name).getColor()
            ));
        }
    }

    public void handleJoinLobby(UUID playerID) {
        if (this.clients.isEmpty()){
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

    public void handleRejoinLobby(UUID playerId) {
        if (!this.disconnectedClients.containsKey(playerId)) {
            this.serverController.sendMessage(playerId, new Error.GenericServerError("player already connected"));
            return;
        }
        if (this.singlePlayerTimeoutTask!=null) this.singlePlayerTimeoutTask.cancel(false);

        this.disconnectedClients.remove(playerId);
        this.serverController.sendMessage(playerId, new Update.LobbyJoinedUpdate(
                new LobbyInfo(this.lobbyId, this.getNumPlayers(), this.getCurrentPlayers()),
                this.getPlayersInfo()
        ));
        String name = this.clients.get(playerId);
        this.innerBroadcast(new Update.PlayerReconnectionUpdate(name));
        System.out.println(name + " reconnected to lobby " + this.lobbyId);
        if (this.singlePlayerPause && this.gameStopped){
            this.gameStopped=false;
            this.singlePlayerPause=false;
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

    public void handleSinglePlayerTimeout() {
        if (this.gameStopped && this.singlePlayerPause) {
            this.gameEnded=true;
            this.executor.delegate(new GameCommand.EndGameCommand());
        }
    }

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


    public void broadcast(Update update) {
        if (this.gameStopped) return;
        for (UUID id : this.clients.keySet()) {
            if (!this.disconnectedClients.containsKey(id))
                this.serverController.sendMessage(id, update);
        }
    }

    public void updatePlayer(String name, Update update) {
        if (this.gameStopped)return;
        this.clients.entrySet().stream()
                .filter(e -> e.getValue().equals(name))
                .filter(e->!this.disconnectedClients.containsKey(e.getKey()))
                .findFirst()
                .ifPresent(e -> this.serverController.sendMessage(e.getKey(), update));
    }

    private void innerBroadcast(Update update){
        for (UUID id : this.clients.keySet()) {
            if (!this.disconnectedClients.containsKey(id))
                this.serverController.sendMessage(id, update);
        }
    }

    private void innerUpdatePlayer(String name, Update update){
        this.clients.entrySet().stream()
                .filter(e -> e.getValue().equals(name))
                .filter(e->!this.disconnectedClients.containsKey(e.getKey()))
                .findFirst()
                .ifPresent(e -> this.serverController.sendMessage(e.getKey(), update));
    }

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