package it.polimi.ingsw.am43.network.socket.server;

import it.polimi.ingsw.am43.controller.ServerController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    final ServerSocket listenSocket;
    final ServerController controller;

    public SocketServer(int port, ServerController controller) throws IOException {
        this.listenSocket = new ServerSocket(port);
        this.controller = controller;
    }

    public void runServer() throws IOException {
        Socket clientSocket;
        SocketClientConnection connection;
        while ((clientSocket = this.listenSocket.accept()) != null) {
            connection=new SocketClientConnection(this.controller,clientSocket);//TODO register here
        }
    }
}
