package it.polimi.ingsw.am43.network.connections;

import it.polimi.ingsw.am43.network.command.CommandReceiver;
import it.polimi.ingsw.am43.network.rmi.ClientRMIConnection;
import it.polimi.ingsw.am43.network.rmi.VirtualClientRmi;
import it.polimi.ingsw.am43.network.socket.server.SocketClientConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

/**
 * Builds server-side client connections, injecting into each one the shared
 * command receiver (the {@code ServerController}) and the connection manager. It
 * exists so that the socket doorman and the RMI access point can create a
 * connection without knowing about those collaborators, and so the same wiring is
 * reused for both transports.
 */
public class ConnectionFactory {
    private final CommandReceiver commandReceiver;
    private final ConnectionHandler manager;

    /**
     * @param commandReceiver the receiver to which incoming commands are forwarded
     * @param manager         the registry connections register themselves into
     */
    public ConnectionFactory(CommandReceiver commandReceiver, ConnectionHandler manager){
        this.commandReceiver=commandReceiver;
        this.manager=manager;
    }

    /**
     * Creates a socket-based server-side connection over already-opened streams.
     *
     * @param id     the player's unique identifier
     * @param socket the accepted client socket
     * @param in     the reader already used during the handshake
     * @param out    the writer already used during the handshake
     * @return a new {@link SocketClientConnection}
     * @throws IOException if the connection cannot be set up
     */
    public SocketClientConnection createConnection(UUID id, Socket socket, BufferedReader in, PrintWriter out) throws IOException {
        return new SocketClientConnection(id,this.commandReceiver,this.manager,socket,in,out);
    }

    /**
     * Creates an RMI-based server-side connection bound to the client's stub.
     *
     * @param id        the player's unique identifier
     * @param clientRmi the remote stub of the connecting client
     * @return a new {@link ClientRMIConnection}
     */
    public ClientRMIConnection createConnection(UUID id, VirtualClientRmi clientRmi){
        return new ClientRMIConnection(id,this.commandReceiver,this.manager,clientRmi);
    }
}
