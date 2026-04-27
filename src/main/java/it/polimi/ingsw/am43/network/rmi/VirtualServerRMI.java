package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.network.VirtualServer;

import java.rmi.RemoteException;
import java.util.UUID;

public interface VirtualServerRMI extends VirtualServer {
    void connect(UUID playerId, VirtualClientRmi client) throws RemoteException;
}
