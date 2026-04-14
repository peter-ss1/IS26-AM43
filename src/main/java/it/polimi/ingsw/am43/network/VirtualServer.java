package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;

public interface VirtualServer {
    void connect();
    void sendCommand(ServerCommand command);
    void sendCommand(GameCommand command);
}
