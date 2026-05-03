package it.polimi.ingsw.am43.network.Connections;

import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;

public interface ServerConnection{

    void sendCommand(GameCommand command);
    void sendCommand(ServerCommand command);
    void open() throws Exception;//TODO specify
    void close();

}
