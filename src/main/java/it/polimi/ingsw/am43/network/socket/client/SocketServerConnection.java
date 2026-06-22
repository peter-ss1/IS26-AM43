package it.polimi.ingsw.am43.network.socket.client;

import it.polimi.ingsw.am43.client.HeartBeat;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.connections.PersistentServerConnection;
import it.polimi.ingsw.am43.network.connections.ServerConnectionUser;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.MessageReceiver;
import it.polimi.ingsw.am43.network.socket.VirtualClientSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Client-side "brain" representing the connection to the server over a socket. It
 * owns the {@link SocketServerListener} (the "ear"), the {@link SocketServerHandler}
 * (the "mouth") and the {@link HeartBeat}, and coordinates the whole lifecycle:
 * opening (with handshake and retries), sending commands, receiving messages and
 * tearing down. A {@link ReadWriteLock} guards usage (read) against open/disconnect
 * (write). It is the mirror image of the server-side {@code SocketClientConnection}.
 */
public class SocketServerConnection implements PersistentServerConnection, VirtualClientSocket {

    private final String ip;
    private final int port;
    private final UUID playerId;
    private final ServerConnectionUser connectionUser;
    private final MessageReceiver messageReceiver;
    private HeartBeat heartBeat;

    private  SocketServerListener listener;
    private SocketServerHandler remote;
    private  Socket socket;
    private volatile long lastPong;
    private AtomicBoolean connected;
    private final ReadWriteLock lock;

    /**
     * @param ip              the server host to connect to
     * @param port            the server port to connect to
     * @param connectionUser  callback notified when the server connection is lost
     * @param messageReceiver where incoming messages are forwarded (the client controller)
     * @param id              this client's persistent unique identifier
     */
    public SocketServerConnection(String ip, int port,ServerConnectionUser connectionUser, MessageReceiver messageReceiver, UUID id){
        this.ip=ip;
        this.port=port;
        this.heartBeat= new HeartBeat(this);
        this.connectionUser= connectionUser;
        this.messageReceiver= messageReceiver;
        this.playerId=id;
        this.lastPong=System.currentTimeMillis();
        this.connected=new AtomicBoolean(false);
        this.lock=new ReentrantReadWriteLock();
    }

    /**
     * Sends a pre-game / lobby command to the server through the handler.
     *
     * @param command the {@link ServerCommand} to send
     */
    public void sendCommand(ServerCommand command) {
        this.lock.readLock().lock();
        try {
            this.lock.readLock().unlock();
        }finally {
            this.remote.sendCommand(command);
        }
    }

    /**
     * Sends an in-game command to the server through the handler.
     *
     * @param command the {@link GameCommand} to send
     */
    public void sendCommand(GameCommand command){
        this.lock.readLock().lock();
        try {
            this.remote.sendCommand(command);
        }finally {
            this.lock.readLock().unlock();
        }
    }

    /** Technical shutdown: stops heartbeat and listener and closes the socket. */
    public void close(){
        this.heartBeat.stop();
        this.listener.stop();
        try {
            this.socket.close();
        }catch (IOException e){System.out.println(e.getMessage());}
    }

    /**
     * Connects to the server and performs the client side of the handshake: sends
     * this client's UUID, waits for the echo to confirm identity, then wires up
     * handler, listener and heartbeat. On failure it keeps retrying every few
     * seconds until the connection succeeds.
     */
    public void open(){
        this.lock.writeLock().lock();
        try {
            while (this.connected.compareAndSet(false,true)){
                try {
                    Socket socket = new Socket(ip, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(this.playerId.toString());
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    socket.setSoTimeout(5000);
                    String uuidString = in.readLine();
                    UUID playerId = UUID.fromString(uuidString);
                    socket.setSoTimeout(0);
                    if(!playerId.equals(this.playerId)) throw new Exception();
                    this.socket=socket;
                    this.remote = new SocketServerHandler(out);
                    this.listener = new SocketServerListener(this, in);
                    this.heartBeat=new HeartBeat(this);
                    this.listener.start();
                    this.heartBeat.start();
                }catch (Exception e){
                    this.connected.set(false);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        continue;
                    }
                }
            }
        }finally {
            this.lock.writeLock().unlock();
        }

    }

    /**
     * Receives a message read by the listener and forwards it to the client
     * controller. Despite the name, this does not write to the network.
     *
     * @param message the incoming message
     */
    public void sendMessage(Message message){
        this.messageReceiver.receiveMessage(message);
    }

    /** Handles a pong from the server: refreshes the server-liveness timestamp. */
    public void pong(){
        this.updateLastPong();
    }


    /** Sends a ping to the server (invoked periodically by the heartbeat). */
    public void ping(){
        this.lock.readLock().lock();
        try {
            this.remote.ping();
        }finally {
            this.lock.readLock().unlock();
        }
    }

    /** @return the timestamp (millis) of the last pong received from the server */
    public long getLastPong(){
        return this.lastPong;
    }

    /** Refreshes the last-pong timestamp to the current time. */
    public void updateLastPong(){
        this.lastPong=System.currentTimeMillis();
    }

    /**
     * Logical shutdown performed once: closes the connection and notifies the
     * connection user, which triggers a reconnection attempt.
     */
    public void disconnect() {
        this.lock.writeLock().lock();
        try {
            if (this.connected.compareAndSet(true,false)){
                this.close();
                this.connectionUser.notifyDisconnection();
            }
        }finally {
            this.lock.writeLock().unlock();
        }
    }

}
