package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.network.Command;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerSocketHandler implements VirtualServerSocket{
    final private PrintWriter output;
    final private ObjectMapper mapper;


    public ServerSocketHandler(BufferedWriter output) {
        this.output = new PrintWriter(output);
        this.mapper= new ObjectMapper();
    }

    @Override
    public void sendCommand(Command command) {
        try {
            String jsonCommand = mapper.writeValueAsString(command);
            output.println(jsonCommand);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

}
