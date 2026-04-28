package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.SinglePersistentClientConnection;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.DataClientToServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class SocketClientConnection implements SinglePersistentClientConnection {
    final ServerController serverController;
    final BufferedReader input;
    final SocketClientHandler remote;
    final Socket socket;
    final Thread loop;
    private volatile long lastPing;

    public SocketClientConnection(ServerController controller, Socket socket) throws IOException {
        this.serverController = controller;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.remote=new SocketClientHandler(socket);
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
                            this.updateLastPing();
                            this.remote.pong();
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

    public VirtualClient getRemote(){
        return this.remote;
    }
    public void disconnect(){

    }

    public long getLastPing() {
        return lastPing;
    }
    public void updateLastPing(){
        this.lastPing=System.currentTimeMillis();
    }
    public void notifyDisconnection(){

    }
}
