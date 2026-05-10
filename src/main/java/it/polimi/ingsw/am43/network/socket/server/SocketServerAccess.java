package it.polimi.ingsw.am43.network.socket.server;

import it.polimi.ingsw.am43.network.connections.ConnectionFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class SocketServerAccess {

    final ServerSocket listenSocket;
    final ConnectionFactory connectionFactory;

    public SocketServerAccess(int port, ConnectionFactory connectionFactory) throws IOException {
        this.listenSocket = new ServerSocket(port);
        this.connectionFactory=connectionFactory;
    }

    public void runServer() throws IOException {
        while (true) {
            Socket clientSocket = this.listenSocket.accept();
            new Thread(()->handshake(clientSocket)).start();
        }
    }

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
            System.err.println("Handshake failed");
            e.printStackTrace();
            try { clientSocket.close(); } catch (Exception ignored) {}
        }
    }
}
