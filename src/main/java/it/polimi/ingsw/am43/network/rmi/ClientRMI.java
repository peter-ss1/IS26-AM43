package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.message.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientRMI extends UnicastRemoteObject implements VirtualClientRmi {
    private final ServerRMIConnection connection;

    public ClientRMI(ServerRMIConnection connection) throws RemoteException {
        super();
        this.connection= connection;
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        this.connection.sendMessage(message);
    }

}
