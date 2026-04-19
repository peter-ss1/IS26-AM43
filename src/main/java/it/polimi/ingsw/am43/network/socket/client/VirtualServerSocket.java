package it.polimi.ingsw.am43.network.socket.client;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.rmi.RemoteException;

public interface VirtualServerSocket extends VirtualServer {
    void sendCommand(ServerCommand command) throws RemoteException;
    void sendCommand(GameCommand command) throws RemoteException;
}
