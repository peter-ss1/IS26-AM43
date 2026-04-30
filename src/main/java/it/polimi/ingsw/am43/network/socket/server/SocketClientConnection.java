package it.polimi.ingsw.am43.network.socket.server;


import it.polimi.ingsw.am43.controller.ServerController;

import it.polimi.ingsw.am43.network.ServerBidirectionalConnection;

import it.polimi.ingsw.am43.network.command.GameCommand;

import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;

import it.polimi.ingsw.am43.network.socket.VirtualClientSocket;
import it.polimi.ingsw.am43.network.socket.VirtualServerSocket;

import java.io.*;
import java.net.Socket;

import java.util.UUID;

public class SocketClientConnection implements ServerBidirectionalConnection, VirtualServerSocket, VirtualClientSocket {
    final ServerController serverController;
    final SocketClientHandler remote;
    final SocketClientListener listener;
    final Socket socket;
    private volatile long lastPing;

    public SocketClientConnection(ServerController controller, Socket socket) throws IOException {
        this.serverController = controller;
        this.listener = new SocketClientListener(this,socket);
        this.remote=new SocketClientHandler(socket);
        this.socket=socket;
        this.listener.start();
    }

    public void sendMessage(Message message){
        this.remote.sendMessage(message);
    };

    public void sendCommand(ServerCommand command){
        this.serverController.addToQueue(command);
    };
    public void sendCommand(GameCommand command){
        this.serverController.addToQueue(command);
    };
    public void connect(){

    }
    public void closeServerConnection(){

    }

    public void ping(UUID id){
        this.updateLastPing();
    }

    public void pong(){
        this.remote.pong();
    }
    public long getLastPing() {
        return lastPing;
    }
    public void updateLastPing(){
        this.lastPing=System.currentTimeMillis();
    }
    public void notifyDisconnection(){
        try {
            this.listener.stop();
            this.socket.close();
            //TODO
        }catch (IOException e){System.out.println(e.getMessage());}
    }
}
