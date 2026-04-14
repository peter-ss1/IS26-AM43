package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;

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
        this.serverController = controller;
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

    public void sendMessage(Message update){
        try {
            String jsonUpdate = mapper.writeValueAsString(update);
            output.println(jsonUpdate);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

    public void sendCommand(Command command){
        this.serverController.addToQueue((ServerCommand) command);
    }

    @Override
    public void sendMessage(Update update) {

    }

    @Override
    public void sendMessage(Error error) {

    }
}
