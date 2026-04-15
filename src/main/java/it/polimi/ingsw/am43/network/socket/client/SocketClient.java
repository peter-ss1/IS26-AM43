package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;
import it.polimi.ingsw.am43.network.socket.server.VirtualClientSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SocketClient implements VirtualClientSocket {

    private final BufferedReader input;
    private final ServerSocketHandler output;
    private final ClientController clientController;

    public SocketClient(BufferedReader input, BufferedWriter output, ClientController clientController){
        this.input=input;
        this.output= new ServerSocketHandler(output);
        this.clientController=clientController;
    }

    private void run() {
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
            try{
                message = UtilsJSON.mapper.readValue(jsonMessage,Update.class);
                this.sendMessage(message);
            } catch (JsonProcessingException e) {e.printStackTrace();}
        }

    }

    @Override
    public void sendMessage(Message message){
        //this.clientController.addToQueue(message);
    }

}
