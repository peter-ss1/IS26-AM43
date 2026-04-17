package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.update.Update;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;
import it.polimi.ingsw.am43.network.socket.server.VirtualClientSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SocketClient {

    private final BufferedReader input;
    private final ClientController clientController;

    public SocketClient(BufferedReader input, BufferedWriter output, ClientController clientController) {
        this.input = input;
        this.clientController = clientController;
        this.clientController.setRemoteModel(new ServerSocketHandler(output));
    }

    public void run() {
        new Thread(() -> {
            try {
                runVirtualServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void runVirtualServer() throws IOException {
        String jsonMessage;
        Message message;
        while ((jsonMessage = input.readLine()) != null) {
            try {
                message = UtilsJSON.mapper.readValue(jsonMessage, Update.class);
                this.clientController.addToQueue(message);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }

}
