package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;

public abstract class Command implements Serializable {
    protected final UUID playerId;

    protected Command(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public abstract void execute(ServerController serverController) throws RemoteException;
    public abstract void execute(GameController gameController) throws RemoteException;
}
