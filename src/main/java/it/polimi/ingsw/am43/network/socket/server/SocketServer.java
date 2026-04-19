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
        while ((clientSocket = this.listenSocket.accept()) != null) {
            InputStreamReader socketRx = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter socketTx = new OutputStreamWriter(clientSocket.getOutputStream());

            ClientSocketHandler handler = new ClientSocketHandler(
                    this.controller,
                    new BufferedReader(socketRx),
                    new PrintWriter(socketTx,true)
            );

            new Thread(() -> {
                try {
                    handler.runVirtualView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            System.out.println("client connected");
        }
    }
}
