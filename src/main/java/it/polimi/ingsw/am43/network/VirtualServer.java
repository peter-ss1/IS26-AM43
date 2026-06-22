package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * Protocol-agnostic contract for everything that can act as a server endpoint,
 * i.e. an object a client is able to send a command to. Both the socket and the
 * RMI implementations ultimately implement this interface, so a client can issue
 * commands without knowing the underlying transport. It extends {@link Remote} so
 * the same contract can be exported through RMI.
 */
public interface VirtualServer extends Remote {

    /**
     * Forwards a pre-game / lobby command to the server.
     *
     * @param command the {@link ServerCommand} to deliver
     * @throws RemoteException if the call fails while travelling over RMI
     */
    void sendCommand(ServerCommand command) throws RemoteException;

    /**
     * Forwards an in-game command to the server.
     *
     * @param command the {@link GameCommand} to deliver
     * @throws RemoteException if the call fails while travelling over RMI
     */
    void sendCommand(GameCommand command) throws RemoteException;
}
