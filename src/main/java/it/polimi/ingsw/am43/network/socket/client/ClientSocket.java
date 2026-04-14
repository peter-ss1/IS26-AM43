package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am43.network.Update;
import it.polimi.ingsw.am43.network.socket.server.VirtualClientSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ClientSocket implements VirtualClientSocket {

    private final BufferedReader input;
    private final ServerSocketHandler output;
    private final ClientModel model;
    private final ObjectMapper mapper;

    public ClientSocket(BufferedReader input, BufferedWriter output, ClientModel model){
        this.input=input;
        this.output= new ServerSocketHandler(output);
        this.model=model;
        this.mapper=new ObjectMapper();
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
        String jsonUpdate;
        Update update;
        while ((jsonUpdate = input.readLine()) != null) {
            try{
                update=mapper.readValue(jsonUpdate,Update.class);
                this.sendUpdate(update);
            } catch (JsonProcessingException e) {e.printStackTrace();}
        }

    }


    @Override
    public void sendUpdate(Update update){
        this.model.update(update);
    }
}
