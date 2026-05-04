package it.polimi.ingsw.am43.network.socket.server;


import it.polimi.ingsw.am43.network.command.CommandReceiver;
import it.polimi.ingsw.am43.network.connections.ConnectionHandler;

import it.polimi.ingsw.am43.network.connections.PersistentClientConnection;

import it.polimi.ingsw.am43.network.command.GameCommand;

import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;

import it.polimi.ingsw.am43.network.socket.VirtualServerSocket;

import java.io.*;
import java.net.Socket;

import java.util.UUID;

public class SocketClientConnection implements VirtualServerSocket, PersistentClientConnection {

    final CommandReceiver commandReceiver;
    final ConnectionHandler connectionHandler;
    final SocketClientHandler remote;
    final SocketClientListener listener;
    final Socket socket;
    final UUID playerId;
    private volatile long lastPing;

    public SocketClientConnection(UUID id, CommandReceiver commandReceiver, ConnectionHandler connectionHandler, Socket socket, BufferedReader in, PrintWriter out){

        this.playerId=id;
        this.commandReceiver=commandReceiver;
        this.connectionHandler = connectionHandler;
        this.listener = new SocketClientListener(this,in);
        this.remote=new SocketClientHandler(out);
        this.socket=socket;
        this.listener.start();
    }

    public void sendMessage(Message message){
        this.remote.sendMessage(message);
    };

    public void sendCommand(ServerCommand command){
        this.commandReceiver.receiveCommand(command);
    };
    public void sendCommand(GameCommand command){
        this.commandReceiver.receiveCommand(command);
    };
    public void ping(){
        this.updateLastPing();
        this.remote.pong();
    }

    public void register(){
        this.connectionHandler.connect(this.playerId,this);
    }

    public void disconnect() {
        try {
            this.listener.stop();
            this.socket.close();
            this.connectionHandler.disconnect(this.playerId);
            //TODO
        }catch (IOException e){System.out.println(e.getMessage());}
    }
    public long getLastPing() {
        return lastPing;
    }
    public void updateLastPing(){
        this.lastPing=System.currentTimeMillis();
    }

}
