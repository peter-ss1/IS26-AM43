package it.polimi.ingsw.am43.network.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;


/**
 * Keep-alive probe sent from the client to the server. It carries no payload; its
 * mere arrival lets the server refresh the client's liveness timestamp.
 */
public final class Ping extends DataClientToServer {
    /** Creates a ping. */
    public Ping() {
    }
}

