package it.polimi.ingsw.am43.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface VirtualServerAccessRMI extends Remote {
    VirtualServerRMI connect(UUID playerId, VirtualClientRmi client) throws RemoteException;
}
