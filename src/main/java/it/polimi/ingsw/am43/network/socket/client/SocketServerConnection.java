package it.polimi.ingsw.am43.network.socket.client;

import it.polimi.ingsw.am43.client.HeartBeat;
import it.polimi.ingsw.am43.network.connections.PersistentServerConnection;

import it.polimi.ingsw.am43.network.connections.ServerConnectionUser;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.*;
import it.polimi.ingsw.am43.network.socket.VirtualClientSocket;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class SocketServerConnection implements PersistentServerConnection, VirtualClientSocket {

    private final String ip;
    private final int port;
    private final UUID playerId;
    private final ServerConnectionUser connectionUser;
    private final MessageReceiver messageReceiver;
    private final HeartBeat heartBeat;

    private  SocketServerListener listener;
    private SocketServerHandler remote;
    private  Socket socket;
    private volatile long lastPong;

    public SocketServerConnection(String ip, int port,ServerConnectionUser connectionUser, MessageReceiver messageReceiver, UUID id){
        this.ip=ip;
        this.port=port;
        this.heartBeat= new HeartBeat(this);
        this.connectionUser= connectionUser;
        this.messageReceiver= messageReceiver;
        this.playerId=id;
        this.lastPong=System.currentTimeMillis();
    }

    public void sendCommand(ServerCommand command) {
        this.remote.sendCommand(command);
    }
    public void sendCommand(GameCommand command){
        this.remote.sendCommand(command);
    }
    public void close(){
        this.heartBeat.stop();
        this.listener.stop();
        try {
            this.socket.close();
        }catch (IOException e){System.out.println(e.getMessage());}
    }
    public boolean open(){
        try {
            Socket socket = new Socket(ip, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(this.playerId.toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socket.setSoTimeout(5000);
            String uuidString = in.readLine();
            System.out.println("ok");
            UUID playerId = UUID.fromString(uuidString);
            socket.setSoTimeout(0);
            if(!playerId.equals(this.playerId)) throw new Exception();//TODO exception
            this.socket=socket;
            this.remote = new SocketServerHandler(out);
            this.listener = new SocketServerListener(this, in);
        }catch (Exception e){return false;}
        this.listener.start();
        this.heartBeat.start();
        return true;
    }

    public void sendMessage(Message message){
        this.messageReceiver.receiveMessage(message);
    }
    public void pong(){
        this.updateLastPong();
    }


    public void ping(){
        this.remote.ping();
    }
    public long getLastPong(){
        return this.lastPong;
    }
    public void updateLastPong(){
        this.lastPong=System.currentTimeMillis();
    }
    public void disconnect() {
        this.close();
        this.connectionUser.notifyDisconnection();
    }

}
