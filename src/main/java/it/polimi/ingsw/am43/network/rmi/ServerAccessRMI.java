package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.network.connections.ConnectionFactory;
import it.polimi.ingsw.am43.network.connections.MultiPersistentClientConnection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class ServerAccessRMI extends UnicastRemoteObject implements VirtualServerAccessRMI {
    private final ConnectionFactory connectionFactory;


    public ServerAccessRMI(ConnectionFactory connectionFactory) throws RemoteException {
        this.connectionFactory=connectionFactory;

    }

    @Override
    public VirtualServerRMI connect(UUID playerID, VirtualClientRmi client) throws RemoteException {
        ClientRMIConnection connection= this.connectionFactory.createConnection(playerID,client);
        VirtualServerRMI stub = (VirtualServerRMI) UnicastRemoteObject.exportObject(connection,0);
        connection.register();
        System.out.println("new RMI client connected");
        return stub;
    }
}
