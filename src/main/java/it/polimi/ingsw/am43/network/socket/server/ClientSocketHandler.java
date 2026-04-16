package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am43.controller.GameController;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.Destination;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.error.Error;
import it.polimi.ingsw.am43.network.message.update.Update;
import it.polimi.ingsw.am43.network.socket.CommandPackageJSON;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class ClientSocketHandler implements VirtualClientSocket {

    final ServerController serverController;
    final SocketServer server;
    final BufferedReader input;
    final PrintWriter output;

    public ClientSocketHandler(ServerController controller, SocketServer server, BufferedReader input, PrintWriter output) {
        this.serverController = controller;
        this.server = server;
        this.input = input;
        this.output = output;
    }

    public void runVirtualView() throws IOException {
        String inputData;
        CommandPackageJSON packageJSON;

        while ((inputData= this.input.readLine()) != null) {
            try{
                packageJSON= UtilsJSON.mapper.readValue(inputData, CommandPackageJSON.class);
                switch (packageJSON.getDestination()) {
                    case Destination.SERVER:
                        this.serverController.addToQueue(UtilsJSON.mapper.treeToValue(packageJSON.getCommandJson(), ServerCommand.class));
                        break;
                    case Destination.GAME:
                        this.serverController.addToQueue(UtilsJSON.mapper.treeToValue(packageJSON.getCommandJson(), GameCommand.class));
                        break;
                    default:
                        //this.sendMessage(new Error());
                }
            } catch (JsonProcessingException e) {e.printStackTrace();}
        }
    }

    public void sendMessage(Message update){
        try {
            String jsonUpdate = UtilsJSON.mapper.writeValueAsString(update);
            output.println(jsonUpdate);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

}
