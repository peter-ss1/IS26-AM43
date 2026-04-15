package it.polimi.ingsw.am43.network.socket.server;

import it.polimi.ingsw.am43.controller.ServerController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer{

    final ServerSocket listenSocket;
    final ServerController controller;

    public SocketServer(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
        this.controller = new ServerController();
    }

    private void runServer() throws IOException {
        Socket clientSocket = null;
        while ((clientSocket = this.listenSocket.accept()) != null) {
            InputStreamReader socketRx = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter socketTx = new OutputStreamWriter(clientSocket.getOutputStream());

            ClientSocketHandler handler = new ClientSocketHandler(
                    this.controller,
                    this,
                    new BufferedReader(socketRx),
                    new PrintWriter(socketTx)
            );


            new Thread(() -> {
                try {
                    handler.runVirtualView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
