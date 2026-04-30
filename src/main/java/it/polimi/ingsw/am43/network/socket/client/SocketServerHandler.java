package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;
import it.polimi.ingsw.am43.network.socket.VirtualServerSocket;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class SocketServerHandler implements VirtualServerSocket {
    private final PrintWriter output;

    public SocketServerHandler(Socket socket)throws IOException {
        this.output=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
    }

    public void sendCommand(ServerCommand serverCommand){
        try {
            String command = UtilsJSON.mapper.writeValueAsString(serverCommand);
            output.println(command);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

    public void sendCommand(GameCommand gameCommand){
        try {
            String command = UtilsJSON.mapper.writeValueAsString(gameCommand);
            output.println(command);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

    public void ping(UUID id){
        try {
            String pingJson = UtilsJSON.mapper.writeValueAsString(new Ping(id));
            output.println(pingJson);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }
}

