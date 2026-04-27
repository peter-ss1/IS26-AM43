package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.message.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientRMI extends UnicastRemoteObject implements VirtualClientRmi {
    private final ClientController controller;
    private volatile long lastPing;

    public ClientRMI(ClientController controller) throws RemoteException {
        super();
        this.controller = controller;
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        this.controller.addToQueue(message);
    }

}
