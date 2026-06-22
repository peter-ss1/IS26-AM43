package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.network.connections.ConnectionHandler;
import it.polimi.ingsw.am43.network.connections.PersistentClientConnection;

import it.polimi.ingsw.am43.network.command.CommandReceiver;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;

import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Server-side "brain" representing a single connected client over RMI. Unlike the
 * socket version it has no handler or listener: {@code remote} is the client's
 * stub, so calling it dispatches the message across the network directly, and
 * incoming commands arrive as direct remote invocations of this object's methods.
 * Incoming commands are forwarded to the {@link CommandReceiver}. It is the RMI
 * counterpart of {@code SocketClientConnection}.
 */
public class ClientRMIConnection implements PersistentClientConnection, VirtualServerRMI {
    private final VirtualClientRmi remote;
    private final CommandReceiver commandReceiver;
    private final ConnectionHandler connectionHandler;
    private  final UUID playerId;
    private volatile long lastPing;
    private final AtomicBoolean connected;

    /**
     * @param id                the player's unique identifier
     * @param commandReceiver   where incoming commands are forwarded (the server controller)
     * @param connectionHandler the registry the connection registers into
     * @param clientRmi         the remote stub of the connecting client
     */
    public ClientRMIConnection(UUID id, CommandReceiver commandReceiver, ConnectionHandler connectionHandler, VirtualClientRmi clientRmi){
        this.commandReceiver=commandReceiver;
        this.connectionHandler = connectionHandler;
        this.playerId=id;
        this.remote=clientRmi;
        this.lastPing=System.currentTimeMillis();
        this.connected=new AtomicBoolean(false);
    }

    /**
     * Receives an in-game command (remotely invoked by the client) and forwards
     * it to the command receiver.
     *
     * @param command the incoming {@link GameCommand}
     * @throws RemoteException if the remote invocation fails
     */
    public void sendCommand(GameCommand command) throws RemoteException{
        this.commandReceiver.receiveCommand(command);
    };

    /**
     * Receives a pre-game command (remotely invoked by the client) and forwards
     * it to the command receiver.
     *
     * @param command the incoming {@link ServerCommand}
     * @throws RemoteException if the remote invocation fails
     */
    public void sendCommand(ServerCommand command) throws RemoteException{
        this.commandReceiver.receiveCommand(command);
    };

    /**
     * Handles a ping (remotely invoked by the client) by refreshing liveness. The
     * client's keep-alive acknowledgement is the successful return of this call.
     *
     * @throws RemoteException if the remote invocation fails
     */
    public void ping() throws RemoteException{
        this.updateLastPing();
    }

    /** Activates the connection exactly once by registering it in the manager. */
    public void register(){
        if (this.connected.compareAndSet(false,true)){
            this.connectionHandler.connect(this.playerId,this);
        }
    };

    /**
     * Pushes a message to the client by invoking its remote stub.
     *
     * @param message the message to send to the client
     */
    public void sendMessage(Message message){
        try {
            this.remote.sendMessage(message);
        }catch (RemoteException e){
            //this.disconnect();
        }

    };

    /** @return the timestamp (millis) of the last ping received from the client */
    public long getLastPing() {
        return lastPing;
    }

    /** Refreshes the last-ping timestamp to the current time. */
    public void updateLastPing(){
        this.lastPing=System.currentTimeMillis();
    }

    /**
     * Tears the connection down exactly once: unexports this remote object and
     * removes it from the registry.
     */
    public void disconnect(){
        if (this.connected.compareAndSet(true,false)){
            try {
                java.rmi.server.UnicastRemoteObject.unexportObject(this, true);
            } catch (java.rmi.NoSuchObjectException e) {
            }
            this.connectionHandler.disconnect(this.playerId,this);
        }
    }
}
