package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.command.*;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.Pong;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;
import it.polimi.ingsw.am43.network.socket.VirtualClientSocket;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class SocketClientHandler implements VirtualClientSocket {
    final PrintWriter output;

    public SocketClientHandler( Socket socket) throws IOException {
        this.output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
    }

    public void sendMessage(Message message) {
        try {
            String jsonMessage = UtilsJSON.mapper.writeValueAsString(message);
            output.println(jsonMessage);
        } catch (JsonProcessingException e) {
            System.out.println("Parsing error:" + e.getMessage());
        }
    }

    public void pong(){
        try {
            String jsonPong = UtilsJSON.mapper.writeValueAsString(new Pong());
            this.output.println(jsonPong);
        } catch (JsonProcessingException e) {
            System.out.println("Parsing error:" + e.getMessage());
        }
    }


}
