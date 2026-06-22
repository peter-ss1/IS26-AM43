package it.polimi.ingsw.am43.network.socket.server;

import it.polimi.ingsw.am43.network.connections.ConnectionFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * Socket entry point on the server side: the "doorman". It owns the listening
 * {@link ServerSocket}, accepts incoming TCP connections and performs the initial
 * handshake with each client before handing the connection over to a
 * {@link SocketClientConnection} built through the {@link ConnectionFactory}.
 */
public class SocketServerAccess {

    final ServerSocket listenSocket;
    final ConnectionFactory connectionFactory;

    /**
     * Opens the listening socket on the given port.
     *
     * @param port              the TCP port the server listens on
     * @param connectionFactory factory used to build a connection for each accepted client
     * @throws IOException if the server socket cannot be opened on the given port
     */
    public SocketServerAccess(int port, ConnectionFactory connectionFactory) throws IOException {
        this.listenSocket = new ServerSocket(port);
        this.connectionFactory=connectionFactory;
    }

    /**
     * Accept loop: blocks on {@code accept()} and, for every incoming client,
     * spawns a dedicated thread that runs the handshake. The accept thread itself
     * never blocks on I/O, so it can keep accepting further clients in parallel.
     *
     * @throws IOException if accepting a connection fails
     */
    public void runServer() throws IOException {
        while (true) {
            Socket clientSocket = this.listenSocket.accept();
            new Thread(()->handshake(clientSocket)).start();
        }
    }

    /**
     * Initial handshake with a freshly accepted client: reads the client UUID
     * (its first line), echoes it back as confirmation, builds the
     * {@link SocketClientConnection} reusing the already-opened streams and
     * activates it through {@link SocketClientConnection#register()}. On any
     * failure the socket is closed.
     *
     * @param clientSocket the socket of the newly accepted client
     */
    public void handshake(Socket clientSocket){
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String uuidString = in.readLine();
            if (uuidString == null) {
                throw new Exception();
            }
            UUID playerId = UUID.fromString(uuidString);
            out.println(playerId.toString());
            SocketClientConnection connection=this.connectionFactory.createConnection(playerId, clientSocket,in,out);
            connection.register();
        } catch (Exception e) {
            System.err.println("Error while handshaking:" + e.getMessage());
            try { clientSocket.close(); } catch (Exception ignored) {}
        }
    }
}
