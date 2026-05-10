package it.polimi.ingsw.am43.controller;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.board.ModelInterface;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.exceptions.IllegalPlayerInitializationException;
import it.polimi.ingsw.am43.model.exceptions.OutOfTurnException;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.GameCommandReceiver;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Update;
import it.polimi.ingsw.am43.utils.Executor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameController implements GameObserver, GameCommandReceiver {

    private final ServerController serverController;
    private final ModelInterface model;
    private final ConcurrentHashMap<UUID, String> clients;
    private final ConcurrentHashMap<UUID,String> disconnectedClients;
    private final Executor<GameController> executor;
    private final int lobbyId;
    private volatile boolean gameStarted;
    private volatile boolean gameStopped;
    private final ReadWriteLock lock;


    public GameController(ServerController serverController, ModelInterface model, int lobbyId, String nickname, UUID playerID) {
        this.serverController = serverController;
        this.model = model;
        this.model.setObserver(this);
        this.clients = new ConcurrentHashMap<>();
        this.disconnectedClients= new ConcurrentHashMap<>();
        this.clients.put(playerID, nickname);
        this.executor=new Executor<>(this);
        this.lobbyId = lobbyId;
        this.executor.start();
        this.gameStarted=false;
        this.gameStopped=false;
        this.lock=new ReentrantReadWriteLock();
    }

    // for recovery
    public GameController(ServerController serverController, ModelInterface model, int lobbyId, ConcurrentMap<UUID,String> clients){
        this.serverController = serverController;
        this.model = model;
        this.model.setObserver(this);
        this.disconnectedClients= new ConcurrentHashMap<>(clients);
        this.clients = new ConcurrentHashMap<>(clients);
        this.executor=new Executor<>(this);
        this.lobbyId = lobbyId;
        this.executor.start();
        this.gameStarted=true;
        this.gameStopped=true;
        this.lock=new ReentrantReadWriteLock();
    }

    public void receiveCommand(GameCommand command) {
        this.executor.delegate(command);
    }

    public int getNumPlayers() {
        return this.model.getNumPlayers();
    }

    public Set<UUID> getPlayers(){
        return this.clients.keySet();
    }

    public int getCurrentPlayers() {
        return this.clients.size();
    }

    public int getLobbyId() {
        return this.lobbyId;
    }

    public void broadcast(Update update) {
        this.lock.readLock().lock();
        for (UUID id : this.clients.keySet()) {
            if (!this.disconnectedClients.containsKey(id))
                this.serverController.sendMessage(id, update);
        }
        this.lock.readLock().unlock();
    }


    public void pickCard(int id, UUID playerID){
        PersistencyManager.saveRecovery(new GameRecovery(this.lobbyId,this.model,this.clients),Integer.toString(this.lobbyId));
        try {
            String nickname = this.clients.get(playerID);
            this.model.pickCard(this.model.getCardById(id), this.model.getPlayerByName(nickname));
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

    public void placeTotem(int position, UUID playerID){
        PersistencyManager.saveRecovery(new GameRecovery(this.lobbyId,this.model,this.clients),Integer.toString(this.lobbyId));
        try {
            String nickname = this.clients.get(playerID);
            this.model.placeTotemOnTrack(this.model.getPlayerByName(nickname), position);
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

    public void endTurn(UUID playerID){
        PersistencyManager.saveRecovery(new GameRecovery(this.lobbyId,this.model,this.clients),Integer.toString(this.lobbyId));
        try {
            String nickname = this.clients.get(playerID);
            this.model.endCurrentTurn(this.model.getPlayerByName(nickname));
            this.broadcast(new Update.TurnEndedUpdate(nickname));
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

    //TODO synchronise methods
    public void joinLobby(UUID playerID){
        this.clients.put(playerID, "-");
        this.serverController.sendMessage(playerID, new Update.LobbyJoinedUpdate(new LobbyInfo(this.lobbyId, this.getNumPlayers(), this.getCurrentPlayers()), this.getPlayersInfo()));
        this.broadcast(new Update.NewLobbyJoinUpdate(this.getCurrentPlayers()));
    }

    //TODO remake
    public void rejoinLobby(UUID playerId){
        this.lock.writeLock().lock();//shiflock
        if (!this.disconnectedClients.containsKey(playerId)) {
            this.serverController.sendMessage(playerId, new Error.GenericServerError("player already connected"));
            return;
        }
        this.disconnectedClients.remove(playerId);
        this.lock.writeLock().unlock();
        this.lock.readLock().lock();
        this.broadcast(new Update.ReconnectionLobbyUpdate(this.clients.values().stream().filter(p -> !disconnectedClients.contains(p)).toList())); //TODO nickname dei ricononnessi
        System.out.println(clients.get(playerId) + " reconnected to lobby " + this.lobbyId);
        if (this.gameStarted) {
            //this.model.activatePlayer(this.model.getPlayerByName(this.clients.get(playerId)));
        }
        if (disconnectedClients.isEmpty() && this.gameStarted && this.gameStopped) { //TODO resiliency to not every player
            this.model.restartGame();
            this.gameStopped=false;
        }
        this.lock.readLock().unlock();

    }

    public void joinGame(UUID playerID, String nickname, Color color){
        if (!this.clients.get(playerID).equals("-")) {
            this.serverController.sendMessage(playerID, new Error.GenericServerError("Player already in game"));
            return;
        }
        try {
            this.model.addPlayer(nickname, color);
            this.clients.replace(playerID, "-", nickname);
            this.serverController.sendMessage(playerID, new Update.GameJoinedUpdate(nickname, color));
            this.broadcast(new Update.PlayerAddedUpdate(nickname, color));
            if (this.clients.values().stream().noneMatch(n -> n.equals("-")) && this.getCurrentPlayers() == this.getNumPlayers()){
                this.model.startGame();
                this.gameStarted=true;
                PersistencyManager.saveRecovery(new GameRecovery(this.lobbyId,this.model,this.clients),Integer.toString(this.lobbyId));
            }
        } catch (IllegalMoveException e) {
            this.serverController.sendMessage(playerID, new Error.GenericServerError(e.getMessage()));
        } catch (IllegalStateException e) {
            this.serverController.sendMessage(playerID, new Error.WrongPhaseError(e.getMessage()));
        } catch (IllegalPlayerInitializationException e) {
            this.serverController.sendMessage(playerID, new Error.InvalidPlayerError(e.getMessage()));
        }
    }

    public void notifyDisconnection(UUID id){
        this.lock.writeLock().lock();
        if (!this.gameStarted) {
            this.clients.remove(id);
            this.serverController.putPlayerChoosing(id);
        }
        else this.disconnectedClients.put(id, this.clients.get(id));
        this.lock.writeLock().unlock();
        //TODO notify other player and model when resiliency
        System.out.println(this.disconnectedClients.get(id)+" disconnected from lobby "+this.lobbyId);
    }

    public void notifyConnection(UUID id){
        this.lock.readLock().lock();
        if(this.disconnectedClients.containsKey(id))
            this.serverController.sendMessage(id,new Update.RejoinRequestUpdate(this.disconnectedClients.get(id)));
        this.lock.readLock().unlock();
    }

    private List<ClientPlayer> getPlayersInfo() {
        return this.model.getPlayers().stream()
                .map(player -> new ClientPlayer(player.getNickname(), player.getColor()))
                .toList();
    }
}