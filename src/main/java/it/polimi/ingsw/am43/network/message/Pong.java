package it.polimi.ingsw.am43.network.message;

/**
 * Keep-alive acknowledgement sent from the server to the client in reply to a
 * ping. It carries no payload; its arrival lets the client refresh the
 * server-liveness timestamp.
 */
public final class Pong extends DataServerToClient {
}
