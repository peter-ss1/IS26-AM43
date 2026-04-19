package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServer extends Remote {
    void sendCommand(ServerCommand command) throws RemoteException;
    void sendCommand(GameCommand command) throws RemoteException;
}
