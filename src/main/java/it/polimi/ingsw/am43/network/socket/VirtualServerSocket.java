package it.polimi.ingsw.am43.network.socket;

import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.util.UUID;

public interface VirtualServerSocket extends VirtualServer {
    void sendCommand(GameCommand command);
    void sendCommand(ServerCommand command);
    public void ping();
}
