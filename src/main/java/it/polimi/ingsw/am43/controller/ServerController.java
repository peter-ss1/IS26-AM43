package it.polimi.ingsw.am43.controller;

import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.update.Update;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ServerController {
    public final Map<Integer, GameController> lobbies;
    public final List<VirtualClient> clients;
    public final BlockingQueue<Command> commandQueue;

    public ServerController() {
        this.lobbies = new HashMap<>();
        this.clients = new ArrayList<>();
        this.commandQueue = new LinkedBlockingQueue<>();
        new Thread(this::executor).start();
    }

    public void addToQueue(ServerCommand command) {
        commandQueue.offer(command);
    }

    public void addToQueue(GameCommand command) {
        lobbies.get(command.getLobbyId()).addToQueue(command);
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

    public void addClient(VirtualClient client) {
        clients.add(client);
    }

    public void sendLobbies(VirtualClient client) {
        client.sendMessage(new Update.AvailableLobbiesUpdate(
                lobbies.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getNumPlayers()))));
    }

    public void createLobby(String nickname, Color color, int numPlayers) {
        this.lobbies.put(Collections.max(this.lobbies.keySet())+1, new GameController(this, new Game(numPlayers, nickname, color)));
    }


}
