package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.socket.CommandPackageJSON;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;

public class ServerSocketHandler implements VirtualServerSocket{
    final private PrintWriter output;


    public ServerSocketHandler(BufferedWriter output) {
        this.output = new PrintWriter(output,true);
    }

    public void sendCommand(ServerCommand serverCommand) throws RemoteException {
        try {
            String commandPackage = UtilsJSON.mapper.writeValueAsString(new CommandPackageJSON(serverCommand));
            System.out.println(commandPackage);
            output.println(commandPackage);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

    public void sendCommand(GameCommand gameCommand) throws RemoteException {
        try {
            String commandPackage = UtilsJSON.mapper.writeValueAsString(new CommandPackageJSON(gameCommand));
            output.println(commandPackage);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

}
