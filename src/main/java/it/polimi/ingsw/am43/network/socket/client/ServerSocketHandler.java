package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.socket.CommandPackageJSON;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;

public class ServerSocketHandler implements VirtualServerSocket{
    final private PrintWriter output;


    public ServerSocketHandler(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    public void sendCommand(ServerCommand serverCommand) {
        try {
            String commandPackage = UtilsJSON.mapper.writeValueAsString(new CommandPackageJSON(serverCommand));
            output.println(commandPackage);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

    public void sendCommand(GameCommand gameCommand) {
        try {
            String commandPackage = UtilsJSON.mapper.writeValueAsString(new CommandPackageJSON(gameCommand));
            output.println(commandPackage);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

}
