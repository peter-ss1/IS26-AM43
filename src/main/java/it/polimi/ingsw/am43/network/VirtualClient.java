package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualClient extends Remote {
    void sendMessage(Message message) throws RemoteException;
}
