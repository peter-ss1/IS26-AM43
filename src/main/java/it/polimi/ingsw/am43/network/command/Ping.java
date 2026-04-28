package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;


public final class Ping extends DataClientToServer {
    public Ping(@JsonProperty("playerId") UUID id) {
        super(id);
    }
}

