package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.PersistentServerConnection;
import it.polimi.ingsw.am43.network.message.DataServerToClient;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Pong;
import it.polimi.ingsw.am43.network.message.Update;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;
import it.polimi.ingsw.am43.network.socket.VirtualClientSocket;
import it.polimi.ingsw.am43.network.socket.server.SocketClientConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketServerListener {
    private final BufferedReader input;
    private final Thread loop;
    private volatile boolean active;
    private final SocketServerConnection connection;

    public SocketServerListener(SocketServerConnection connection, Socket socket ) throws IOException{
        this.input=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.connection=connection;
        this.loop=new Thread(this::runLoop);
        this.active=false;
    }

    public void start(){
        this.active=true;
        this.loop.start();
    }
    public void stop(){
        this.active=false;
    }
    private void runLoop(){
        String json;
        DataServerToClient data;
        try{
            while ((json = input.readLine()) != null && this.active) {
                try {
                    data = UtilsJSON.mapper.readValue(json, DataServerToClient.class);
                    switch (data) {
                        case Pong pong:
                            this.connection.pong();
                            break;
                        case Update update:
                            this.connection.sendMessage(update);
                            break;
                        case Error error:
                            this.connection.sendMessage(error);
                            break;
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
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
