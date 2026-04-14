package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;

public class ServerSocketHandler{
    final private PrintWriter output;
    final private ObjectMapper mapper;


    public ServerSocketHandler(BufferedWriter output) {
        this.output = new PrintWriter(output);
        this.mapper= new ObjectMapper();
    }

    public void sendCommand(Command command) {
        try {
            String jsonCommand = mapper.writeValueAsString(command);
            output.println(jsonCommand);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }
}
