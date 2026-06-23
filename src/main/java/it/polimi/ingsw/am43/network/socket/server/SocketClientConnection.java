package it.polimi.ingsw.am43.network.socket.server;


import it.polimi.ingsw.am43.network.command.CommandReceiver;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.connections.ConnectionHandler;
import it.polimi.ingsw.am43.network.connections.PersistentClientConnection;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.socket.VirtualServerSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Server-side "brain" representing a single connected client over a socket: it
 * holds everything the server needs to talk to that client, namely the socket, the
 * {@link SocketClientListener} (the "ear" that reads incoming data) and the
 * {@link SocketClientHandler} (the "mouth" that writes outgoing data). Incoming
 * commands are forwarded to the {@link CommandReceiver}; outgoing messages are
 * delegated to the handler. There is one instance of this class per connected
 * client.
 */
public class SocketClientConnection implements VirtualServerSocket, PersistentClientConnection {

    final CommandReceiver commandReceiver;
    final ConnectionHandler connectionHandler;
    final SocketClientHandler remote;
    final SocketClientListener listener;
    final Socket socket;
    final UUID playerId;
    private volatile long lastPing;
    private final AtomicBoolean connected;

    /**
     * Wires together the listener and handler over the already-opened streams.
     *
     * @param id                the player's unique identifier
     * @param commandReceiver   where incoming commands are forwarded (the server controller)
     * @param connectionHandler the registry the connection registers into
     * @param socket            the underlying client socket
     * @param in                the reader already used during the handshake
     * @param out               the writer already used during the handshake
     */
    public SocketClientConnection(UUID id, CommandReceiver commandReceiver, ConnectionHandler connectionHandler, Socket socket, BufferedReader in, PrintWriter out){

        this.playerId=id;
        this.commandReceiver=commandReceiver;
        this.connectionHandler = connectionHandler;
        this.listener = new SocketClientListener(this,in);
        this.remote=new SocketClientHandler(out);
        this.socket=socket;
        this.lastPing=System.currentTimeMillis();
        this.connected=new AtomicBoolean(false);
    }

    /**
     * Sends a message to the client by delegating to the handler (writes to socket).
     *
     * @param message the message to send to the client
     */
    public void sendMessage(Message message){
        this.remote.sendMessage(message);
    };

    /**
     * Receives a pre-game command that just arrived from the client and forwards
     * it to the command receiver. Despite the name, this does not write to the
     * network.
     *
     * @param command the incoming {@link ServerCommand}
     */
    public void sendCommand(ServerCommand command){
        this.commandReceiver.receiveCommand(command);
    };

    /**
     * Receives an in-game command that just arrived from the client and forwards
     * it to the command receiver. Despite the name, this does not write to the
     * network.
     *
     * @param command the incoming {@link GameCommand}
     */
    public void sendCommand(GameCommand command){
        this.commandReceiver.receiveCommand(command);
    };

    /** Handles a ping from the client: refreshes liveness and replies with a pong. */
    public void ping(){
        this.updateLastPing();
        this.remote.pong();
    }

    /**
     * Activates the connection exactly once: starts the listener thread and
     * registers this connection in the manager so the server can find the client
     * by its UUID.
     */
    public void register(){
        if (this.connected.compareAndSet(false,true)){
            this.listener.start();
            this.connectionHandler.connect(this.playerId,this);
        }

    }

    /**
     * Tears the connection down exactly once: stops the listener, closes the
     * socket and unregisters from the manager.
     */
    public void disconnect() {
        if (this.connected.compareAndSet(true,false)){
            try {
                this.listener.stop();
                this.socket.close();
                this.connectionHandler.disconnect(this.playerId,this);
            }catch (IOException e){System.out.println(e.getMessage());}
        }

    }

    /** @return the timestamp (millis) of the last ping received from the client */
    public long getLastPing() {
        return lastPing;
    }

    /** Refreshes the last-ping timestamp to the current time. */
    public void updateLastPing(){
        this.lastPing=System.currentTimeMillis();
    }

}
