package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.client.HeartBeat;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.PersistentServerConnection;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.DataServerToClient;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Pong;
import it.polimi.ingsw.am43.network.message.Update;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;
import it.polimi.ingsw.am43.network.socket.VirtualServerSocket;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.UUID;

public class SocketServerConnection implements PersistentServerConnection {

    public SocketServerHandler remote;
    private volatile long lastPong;
    private final UUID playerId;
    private final HeartBeat heartBeat;
    private final ClientController clientController;
    private final BufferedReader input;
    private final Thread listener;

    public SocketServerConnection(Socket socket, ClientController clientController, UUID id) throws IOException{
        this.remote=new SocketServerHandler(new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true));
        this.input= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientController=clientController;
        this.playerId=id;
        this.lastPong=System.currentTimeMillis();
        this.heartBeat= new HeartBeat(this);
        this.listener=new Thread(this::runListener);
    }

    public void connect(){
        this.remote.sendCommand(new ServerCommand.RegisterCommand(this.playerId));
        this.listener.start();
        this.heartBeat.start();
    }
    public void disconnect(){

    }
    public VirtualServer getRemote(){
        return this.remote;
    }

    public void runListener(){
        String json;
        DataServerToClient data;
        try{
            while ((json = input.readLine()) != null) {
                try {
                    data = UtilsJSON.mapper.readValue(json, DataServerToClient.class);
                    switch (data) {
                        case Pong pong:
                            this.lastPong = System.currentTimeMillis();
                            break;
                        case Update update:
                            this.clientController.addToQueue(update);
                            break;
                        case Error error:
                            this.clientController.addToQueue(error);
                            break;
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
                //TODO implement
            }
    }

    public void ping(){
        this.remote.ping(this.playerId);
    }
    public long getLastPong(){
        return this.lastPong;
    }
    public void updateLastPong(){
        this.lastPong=System.currentTimeMillis();
    }
    public void notifyDisconnection() {
        this.heartBeat.stop();
        this.clientController.disconnect();
    }


}
