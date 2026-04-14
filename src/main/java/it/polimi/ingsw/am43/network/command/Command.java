package it.polimi.ingsw.am43.network.command;

import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;

import java.util.UUID;

public abstract class Command {
    public abstract void execute(ServerController serverController);
    public abstract void execute(GameController gameController);
}
