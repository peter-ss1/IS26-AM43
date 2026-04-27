package it.polimi.ingsw.am43.network.command;

import java.io.Serializable;
import java.util.UUID;

public abstract sealed class DataClientToServer implements Serializable permits Command, Ping{
    protected final UUID playerId;

    public DataClientToServer(UUID id){
        this.playerId=id;
    }
    public UUID getPlayerId() {
        return playerId;
    }
}
