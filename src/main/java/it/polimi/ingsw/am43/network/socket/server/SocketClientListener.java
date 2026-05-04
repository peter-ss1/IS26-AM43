package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.network.command.DataClientToServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.utils.UtilsJSON;

import java.io.BufferedReader;
import java.io.IOException;

public class SocketClientListener {
    final SocketClientConnection connection;
    final BufferedReader input;
    final Thread loop;
    private volatile boolean active;

    public SocketClientListener(SocketClientConnection connection, BufferedReader in){
        this.connection=connection;
        this.input = in;
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
            while ((inputData = this.input.readLine()) != null && this.active) {
                try {
                    data = UtilsJSON.mapper.readValue(inputData, DataClientToServer.class);
                    switch (data) {
                        case Ping ping:
                            this.connection.ping();
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
                this.connection.disconnect();
            }
        }

    }

}
