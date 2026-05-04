package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface VirtualServerRMI extends VirtualServer, Remote {
    void ping() throws RemoteException;
}
