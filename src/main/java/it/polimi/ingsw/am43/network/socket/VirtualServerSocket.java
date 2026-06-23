package it.polimi.ingsw.am43.network.socket;

import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;

/**
 * Socket-specific specialization of {@link VirtualServer}. On top of forwarding
 * commands it adds {@link #ping()}, since over a raw socket the keep-alive probe
 * must be sent as an explicit message (unlike RMI, where it is implicit).
 */
public interface VirtualServerSocket extends VirtualServer {
    /**
     * Forwards an in-game command to the server.
     *
     * @param command the {@link GameCommand} to send
     */
    void sendCommand(GameCommand command);

    /**
     * Forwards a pre-game / lobby command to the server.
     *
     * @param command the {@link ServerCommand} to send
     */
    void sendCommand(ServerCommand command);

    /** Sends a ping to the server to keep the connection alive. */
    public void ping();
}
