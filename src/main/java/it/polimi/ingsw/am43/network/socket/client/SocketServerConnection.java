package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.client.HeartBeat;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.network.PersistentServerConnection;

import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.*;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.socket.UtilsJSON;
import it.polimi.ingsw.am43.network.socket.VirtualClientSocket;
import it.polimi.ingsw.am43.network.socket.VirtualServerSocket;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.UUID;

public class SocketServerConnection implements PersistentServerConnection, VirtualClientSocket {

    public SocketServerHandler remote;
    private volatile long lastPong;
    private final UUID playerId;
    private final ClientController clientController;
    private final HeartBeat heartBeat;
    private final SocketServerListener listener;
    private final Socket socket;


    public SocketServerConnection(Socket socket, ClientController clientController, UUID id) throws IOException{
        this.socket=socket;
        this.remote=new SocketServerHandler(socket);
        this.listener= new SocketServerListener(this,socket);
        this.heartBeat= new HeartBeat(this);
        this.clientController=clientController;
        this.playerId=id;
        this.lastPong=System.currentTimeMillis();
    }

    @Override
    public void sendCommand(ServerCommand command) {
        this.remote.sendCommand(command);
    }
    @Override
    public void sendCommand(GameCommand command){
        this.remote.sendCommand(command);
    }
    public void connect(){
        this.remote.sendCommand(new ServerCommand.RegisterCommand(this.playerId));
        this.listener.start();
        this.heartBeat.start();
    }
    public void closeServerConnection(){

    }

    public void sendMessage(Message message){
        this.clientController.receiveMessage(message);
    }
    public void pong(){
        this.updateLastPong();
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
        this.listener.stop();
        try {
            this.socket.close();
        }catch (IOException e){System.out.println(e.getMessage());}
        this.clientController.notifyDisconnection();
    }

}
