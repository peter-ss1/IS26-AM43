package it.polimi.ingsw.am43.network.socket;

import it.polimi.ingsw.am43.network.VirtualClient;


/**
 * Socket-specific specialization of {@link VirtualClient}. On top of delivering
 * messages it adds {@link #pong()}, since over a raw socket the keep-alive reply
 * must be sent as an explicit message (unlike RMI, where it is implicit).
 */
public interface VirtualClientSocket extends VirtualClient {
    /** Sends a pong back to the client in response to a ping. */
    public void pong();
}
