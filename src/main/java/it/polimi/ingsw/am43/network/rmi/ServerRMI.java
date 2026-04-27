package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class ServerRMI extends UnicastRemoteObject implements VirtualServerRMI {
    private final ServerController controller;

    public ServerRMI(ServerController controller) throws RemoteException {
        super();
        this.controller = controller;
    }

    @Override
    public void connect(UUID playerID, VirtualClientRmi client) throws RemoteException {
        this.controller.register(playerID, client);
    }

    @Override
    public void sendCommand(ServerCommand command) throws RemoteException {
        this.controller.addToQueue(command);
    }

    @Override
    public void sendCommand(GameCommand command) throws RemoteException {
        this.controller.addToQueue(command);
    }

    public void ping(UUID id) throws RemoteException{
        this.controller.updataLastPong(id);
    }
}
