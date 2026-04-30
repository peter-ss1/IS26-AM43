package it.polimi.ingsw.am43.network.socket;

import it.polimi.ingsw.am43.network.VirtualServer;

import java.util.UUID;

public interface VirtualServerSocket extends VirtualServer {
    public void ping(UUID id);
}
