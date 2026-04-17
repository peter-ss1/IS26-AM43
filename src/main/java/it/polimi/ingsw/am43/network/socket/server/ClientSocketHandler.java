package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.command.Destination;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.socket.CommandPackageJSON;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.UUID;

public class ClientSocketHandler implements VirtualClientSocket {

    final ServerController serverController;
    final BufferedReader input;
    final PrintWriter output;

    public ClientSocketHandler(ServerController controller, BufferedReader input, PrintWriter output) {
        this.serverController = controller;
        this.input = input;
        this.output = output;
    }

    public void runVirtualView() throws IOException {
        String inputData;
        CommandPackageJSON packageJSON;

        if ((inputData = this.input.readLine()) == null) return;
        try {
            packageJSON = UtilsJSON.mapper.readValue(inputData, CommandPackageJSON.class);
            UUID playerId = UtilsJSON.mapper.treeToValue(packageJSON.getCommandJson(), ServerCommand.class).getPlayerId();
            this.serverController.register(playerId, this);
        } catch (JsonProcessingException e) {
            System.err.println("Handshake failed");
            return;
        }

        while ((inputData = this.input.readLine()) != null) {
            try {
                packageJSON = UtilsJSON.mapper.readValue(inputData, CommandPackageJSON.class);
                switch (packageJSON.getDestination()) {
                    case Destination.SERVER:
                        this.serverController.addToQueue(UtilsJSON.mapper.treeToValue(packageJSON.getCommandJson(), ServerCommand.class));
                        break;
                    case Destination.GAME:
                        this.serverController.addToQueue(UtilsJSON.mapper.treeToValue(packageJSON.getCommandJson(), GameCommand.class));
                        break;
                }
            } catch (JsonProcessingException e) {
                System.out.println("Parsing error:" + e.getMessage());
            }
        }
    }

    public void sendMessage(Message update) throws RemoteException {
        try {
            String jsonUpdate = UtilsJSON.mapper.writeValueAsString(update);
            output.println(jsonUpdate);
        } catch (JsonProcessingException e) {
            System.out.println("Parsing error:" + e.getMessage());
        }
    }

}
