package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.update.Update;
import it.polimi.ingsw.am43.network.message.error.Error;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualClient extends Remote {
    void sendMessage(Message message) throws RemoteException;
}
