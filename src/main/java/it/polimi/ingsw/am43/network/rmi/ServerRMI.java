package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.ClientsConnectionManager;
import it.polimi.ingsw.am43.network.MultiPersistentClientConnection;
import it.polimi.ingsw.am43.network.ServerBidirectionalConnection;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class ServerRMI extends UnicastRemoteObject implements VirtualServerRMI {
    private final MultiPersistentClientConnection<ServerBidirectionalConnection> connectionManager;
    private final ServerController controller;

    public ServerRMI(ServerController controller,MultiPersistentClientConnection<ServerBidirectionalConnection> connectionManager) throws RemoteException {
        super();
        this.connectionManager = connectionManager;
        this.controller=controller;
    }

    @Override
    public void connect(UUID playerID, VirtualClientRmi client) throws RemoteException {
        this.controller.register(playerID, new ClientRMIConnection(client,this.controller));
    }

    @Override
    public void sendCommand(ServerCommand command) throws RemoteException {
        this.connectionManager.getCompleteConnection(command.getPlayerId()).sendCommand(command);
    }

    @Override
    public void sendCommand(GameCommand command) throws RemoteException {
        this.connectionManager.getCompleteConnection(command.getPlayerId()).sendCommand(command);
    }

    public void ping(UUID id) throws RemoteException{
        this.connectionManager.updateLastPing(id);
    }
}
