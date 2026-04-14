package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am43.network.Command;
import it.polimi.ingsw.am43.network.Update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientSocketHandler implements VirtualClientSocket {

    final ServerController serverController;
    final ServerSocket server;
    final BufferedReader input;
    final PrintWriter output;
    final ObjectMapper mapper;

    public ClientSocketHandler(ServerController controller, ServerSocket server, BufferedReader input, PrintWriter output) {
        this.controller = controller;
        this.server = server;
        this.input = input;
        this.output = output;
        this.mapper=new ObjectMapper();
    }

    public void runVirtualView() throws IOException {
        String jsonCommand;
        Command command;

        while ((jsonCommand = input.readLine()) != null) {
            try{
                command=mapper.readValue(jsonCommand, Command.class);
                this.sendCommand(command);
            } catch (JsonProcessingException e) {e.printStackTrace();}
        }
    }

    public void sendUpdate(Update update){
        try {
            String jsonUpdate = mapper.writeValueAsString(update);
            output.println(jsonUpdate);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

    public void sendCommand(Command command){
        this.serverController.sendCommand(command);
    }
}
