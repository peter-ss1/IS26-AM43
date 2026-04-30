package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.rmi.RemoteException;
import java.util.UUID;

public interface ServerConnection extends VirtualServer{

    void sendCommand(GameCommand command);
    void sendCommand(ServerCommand command);
    void connect();
    void closeServerConnection();
}
