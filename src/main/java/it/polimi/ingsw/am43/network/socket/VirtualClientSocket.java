package it.polimi.ingsw.am43.network.socket;

import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.message.Message;

import java.rmi.RemoteException;


public interface VirtualClientSocket extends VirtualClient {
    public void pong();
}
