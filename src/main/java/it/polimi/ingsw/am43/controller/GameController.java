package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.model.board.ModelInterface;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.message.error.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameController {
    public final ServerController serverController;
    public final ModelInterface model;
    public final Map<String, VirtualClient> clients;
    public final BlockingQueue<Command> commandQueue;

    public GameController(ServerController serverController, ModelInterface model) {
        this.serverController = serverController;
        this.model = model;
        this.clients = new HashMap<>();
        this.commandQueue = new LinkedBlockingQueue<>();
        new Thread(this::executor).start();
    }

    public void addToQueue(Command command) {
        commandQueue.offer(command);
    }

    private void executor() {
        while (true) {
            Command command;
            try {
                command = commandQueue.take();
                command.execute(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPlayer(VirtualClient client, String nickname, Color color) {

    }


    public Integer getNumPlayers() {
        return this.model.getNumPlayers();
    }

    public void pickCard(int id, String nickname) {
        try {
            this.model.pickCard(this.model.getCardById(id), this.model.getPlayerByName(nickname));
        } catch (IllegalMoveException e) {
            this.clients.get(nickname).sendMessage(new Error.IllegalMoveError(e.getMessage()));
        }
    }
}
