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
import java.rmi.RemoteException;
import java.util.UUID;

public class ClientSocketHandler implements VirtualClientSocket {

    final ServerController serverController;
    final BufferedReader input;
    final PrintWriter output;
    final Socket socket;
    final Thread loop;

    public ClientSocketHandler(ServerController controller, Socket socket) throws IOException {
        this.serverController = controller;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        this.socket=socket;
        this.loop= new Thread(this::runVirtualView);
    }

    public void stop() throws IOException{
        this.socket.close();  // check
        this.loop.interrupt();
    }
    public void runVirtualView(){
        String inputData;
        DataClientToServer data;
        try{
            inputData = this.input.readLine();
            try {
                data = UtilsJSON.mapper.readValue(inputData, ServerCommand.RegisterCommand.class);
                UUID playerId = data.getPlayerId();
                this.serverController.register(playerId, this);
            } catch (JsonProcessingException e) {
                System.out.println("Handshake failed");
                e.printStackTrace();
                return;
            }

            while ((inputData = this.input.readLine()) != null) {
                try {
                    data = UtilsJSON.mapper.readValue(inputData, DataClientToServer.class);
                    switch (data) {
                        case Ping ping:
                            this.serverController.updateLastPing(ping.getPlayerId());
                            this.pong();
                            break;
                        case GameCommand gameCommand:
                            this.serverController.addToQueue(gameCommand);
                            break;
                        case ServerCommand serverCommand:
                            this.serverController.addToQueue(serverCommand);
                            break;
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("Parsing error:" + e.getMessage());
                }
            }
        }catch (IOException e){
            //TODO implement
            //this.serverController.disconnect(this);
        }

    }

    public void sendMessage(Message message) {
        try {
            String jsonMessage = UtilsJSON.mapper.writeValueAsString(message);
            output.println(jsonMessage);
        } catch (JsonProcessingException e) {
            System.out.println("Parsing error:" + e.getMessage());
        }
    }

    private void pong(){
        try {
            String jsonPong = UtilsJSON.mapper.writeValueAsString(new Pong());
            output.println(jsonPong);
        } catch (JsonProcessingException e) {
            System.out.println("Parsing error:" + e.getMessage());
        }
    }

}
