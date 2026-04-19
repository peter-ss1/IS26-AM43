package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.model.board.ModelInterface;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.exceptions.IllegalPlayerInitializationException;
import it.polimi.ingsw.am43.model.exceptions.OutOfTurnException;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Update;

import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class GameController {
    private final ServerController serverController;
    private final ModelInterface model;
    private final ConcurrentMap<UUID, String> clients;
    private final BlockingQueue<Command> commandQueue;
    private final int lobbyId;
    private boolean gameStarted;

    public GameController(ServerController serverController, ModelInterface model, int lobbyId, String nickname, UUID playerID) {
        this.serverController = serverController;
        this.model = model;
        this.clients = new ConcurrentHashMap<>();
        this.clients.put(playerID,nickname);
        this.commandQueue = new LinkedBlockingQueue<>();
        this.lobbyId = lobbyId;
        this.gameStarted = false;
        new Thread(this::executor).start();
    }

    public void addToQueue(Command command) {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void executor() {
        while (true) {
            try {
                Command command = commandQueue.take();
                command.execute(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (RuntimeException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }



    public int getNumPlayers() {
        return this.model.getNumPlayers();
    }

    public int getCurrentPlayers() {
        return this.clients.size();
    }


    private void startGame() {
        this.gameStarted = true;
        broadcast(new Update.GameStartedUpdate(getLobbyId()));
    }

    public void pickCard(int id, UUID playerID) throws RemoteException {
        try {
            String nickname=this.clients.get(playerID);
            this.model.pickCard(this.model.getCardById(id), this.model.getPlayerByName(nickname));
            this.broadcast(new Update.CardPickedUpdate(nickname, id));
        } catch (IllegalMoveException e) {
            this.serverController.sendMessage(playerID,new Error.IllegalMoveError(e.getMessage()));
        } catch (OutOfTurnException e) {
            this.serverController.sendMessage(playerID,new Error.IllegalMoveError(e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.serverController.sendMessage(playerID,new Error.GenericServerError(e.getMessage()));
        }
    }

    public int getLobbyId() {
        return this.lobbyId;
    }

    public void broadcast(Update update) {
        for (UUID id : this.clients.keySet()) {
            try {
                this.serverController.sendMessage(id,update);
            } catch (java.rmi.RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void placeTotem(int position, UUID playerID) throws RemoteException {
        try {
            String nickname=this.clients.get(playerID);
            this.model.placeTotemOnTrack(this.model.getPlayerByName(nickname), position);
            broadcast(new Update.TotemPlacedUpdate(nickname, position));
        } catch (OutOfTurnException e) {
            this.serverController.sendMessage(playerID,new Error.OutOfTurnError(e.getMessage()));
        } catch (IllegalStateException e) {
            this.serverController.sendMessage(playerID,new Error.WrongPhaseError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            this.serverController.sendMessage(playerID,new Error.InvalidTotemPositionError(position, e.getMessage()));
        }
    }

    public void endTurn(UUID playerID) throws RemoteException {
        try {
            String nickname= this.clients.get(playerID);
            this.model.endCurrentTurn(this.model.getPlayerByName(nickname));
            this.broadcast(new Update.TurnEndedUpdate(nickname));
        } catch (OutOfTurnException e) {
            this.serverController.sendMessage(playerID,new Error.OutOfTurnError(e.getMessage()));
        } catch (IllegalStateException e) {
            this.serverController.sendMessage(playerID,new Error.WrongPhaseError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            this.serverController.sendMessage(playerID,new Error.CannotEndTurnError(e.getMessage()));
        }
    }
    //TODO syncronise methods
    public void joinLobby(UUID playerID) throws RemoteException{
        if(gameStarted){
            this.serverController.sendMessage(playerID,new Error.GameAlreadyStartedError());
            return;
        }
        if(this.clients.size()>=this.model.getNumPlayers())
            this.serverController.sendMessage(playerID,new Error.FullLobbyError());
        this.clients.put(playerID,"-");
    }
    public void joinGame(UUID playerID, String nickname, Color color)throws IllegalArgumentException, RemoteException{
        if(!this.clients.get(playerID).equals("-")) throw new IllegalArgumentException("player already in game");
        if(!this.model.getAvailableColors().contains(color)){
            this.serverController.sendMessage(playerID,new Error.ColorAlreadyUsedError(color));
            return;
        }
        if(this.clients.containsValue(nickname)){
            this.serverController.sendMessage(playerID,new Error.NicknameAlreadyUsedInLobbyError(nickname));
            return;
        }
        this.model.addPlayer(nickname,color);
        this.clients.replace(playerID,"-",nickname);
    }

}