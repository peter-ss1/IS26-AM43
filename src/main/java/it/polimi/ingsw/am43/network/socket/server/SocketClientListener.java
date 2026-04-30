package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.SingleClientConnection;
import it.polimi.ingsw.am43.network.SinglePersistentClientConnection;
import it.polimi.ingsw.am43.network.VirtualClient;
import it.polimi.ingsw.am43.network.command.DataClientToServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;

public class SocketClientListener {
    final SocketClientConnection connection;
    final BufferedReader input;
    final Thread loop;
    private volatile boolean active;

    public SocketClientListener(SocketClientConnection connection, Socket socket) throws IOException {
        this.connection=connection;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.loop= new Thread(this::runLoop);
        this.active=false;
    }

    public void start(){
        this.active=true;
        this.loop.start();
    }
    public void stop(){
        this.active=false;
    }

    public void runLoop(){
        if(!this.active)return;
        String inputData;
        DataClientToServer data;
        try{
            inputData = this.input.readLine();
            try {
                data = UtilsJSON.mapper.readValue(inputData, ServerCommand.RegisterCommand.class);
                UUID playerId = data.getPlayerId();
                this.serverController.register(playerId, this.connection);
            } catch (JsonProcessingException e) {
                System.out.println("Handshake failed");
                e.printStackTrace();
                return;
            }

            while ((inputData = this.input.readLine()) != null && this.active) {
                try {
                    data = UtilsJSON.mapper.readValue(inputData, DataClientToServer.class);
                    switch (data) {
                        case Ping ping:
                            this.connection.ping(ping.getPlayerId());
                            this.connection.pong();
                            break;
                        case GameCommand gameCommand:
                            this.connection.sendCommand(gameCommand);
                            break;
                        case ServerCommand serverCommand:
                            this.connection.sendCommand(serverCommand);
                            break;
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("Parsing error:" + e.getMessage());
                }
            }
        }catch (IOException e){
            if (this.active){
                this.active=false;
                this.connection.notifyDisconnection();
            }
        }

    }

}
