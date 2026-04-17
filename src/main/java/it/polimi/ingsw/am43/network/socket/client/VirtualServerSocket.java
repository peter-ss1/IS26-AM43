package it.polimi.ingsw.am43.network.socket.client;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;

import java.rmi.RemoteException;

public interface VirtualServerSocket extends VirtualServer {
    void sendCommand(ServerCommand command) throws RemoteException;
    void sendCommand(GameCommand command) throws RemoteException;
}
