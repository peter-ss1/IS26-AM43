package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.client.HeartBeat;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.connections.PersistentServerConnection;
import it.polimi.ingsw.am43.network.connections.ServerConnectionUser;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.MessageReceiver;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Client-side "brain" representing the connection to the server over RMI. It
 * exports itself as the client stub and holds {@code remote}, the server
 * connection's stub, on which it invokes commands and pings directly across the
 * network. It owns the {@link HeartBeat} and manages the lifecycle (lookup +
 * handshake in {@link #open()}, with retries, and teardown in {@link #disconnect()}).
 * It is the RMI counterpart of {@code SocketServerConnection}.
 */
public class ServerRMIConnection implements PersistentServerConnection, VirtualClientRmi  {

    public VirtualServerRMI remote;
    private volatile long lastPong;
    private HeartBeat heartBeat;
    private final UUID playerID;
    private final ServerConnectionUser connectionUser;
    private final MessageReceiver messageReceiver;
    private final String serverIp;
    private final int port;
    private final String accessPointName;
    private AtomicBoolean connected;
    private final ReadWriteLock lock;

    /**
     * @param serverIp        the server host to connect to
     * @param port            the RMI registry port
     * @param accessPointName the name the access point is bound to in the registry
     * @param connectionUser  callback notified when the server connection is lost
     * @param messageReceiver where incoming messages are forwarded (the client controller)
     * @param id              this client's persistent unique identifier
     */
    public ServerRMIConnection(String serverIp, int port, String accessPointName, ServerConnectionUser connectionUser, MessageReceiver messageReceiver, UUID id) {
        this.port=port;
        this.serverIp=serverIp;
        this.accessPointName=accessPointName;
        this.connectionUser=connectionUser;
        this.messageReceiver=messageReceiver;
        this.playerID=id;
        this.lastPong=System.currentTimeMillis();
        this.connected=new AtomicBoolean(false);
        this.lock=new ReentrantReadWriteLock();
    }

    /**
     * Connects to the server: looks up the access point in the registry, exports
     * this object as the client stub, calls {@code connect()} to perform the
     * handshake and obtain the server stub, then starts the heartbeat. On failure
     * it keeps retrying every few seconds until it succeeds.
     */
    public void open(){
        this.lock.writeLock().lock();
        try {
            while(this.connected.compareAndSet(false,true)){
                try {
                    Registry registry = LocateRegistry.getRegistry(serverIp, this.port);
                    VirtualServerAccessRMI accessRMI = (VirtualServerAccessRMI) registry.lookup(this.accessPointName);
                    VirtualClientRmi stub;
                    try {
                        stub = (VirtualClientRmi) UnicastRemoteObject.exportObject(this, 0);
                    } catch (ExportException e) {
                        stub = (VirtualClientRmi) RemoteObject.toStub(this);
                    }
                    this.remote=accessRMI.connect(this.playerID,stub);
                    this.lastPong=System.currentTimeMillis();
                    this.heartBeat= new HeartBeat(this);
                    this.heartBeat.start();
                }catch (Exception e)  {
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
    /** Technical shutdown: stops the heartbeat. */
    public void close(){
        this.heartBeat.stop();
    }

    /**
     * Sends a pre-game / lobby command to the server by invoking the server stub.
     *
     * @param command the {@link ServerCommand} to send
     */
    public void sendCommand(ServerCommand command){
        this.lock.readLock().lock();
        try {
            this.remote.sendCommand(command);
        }catch (RemoteException e){
        }
        catch (NullPointerException e){System.err.println("Connection not already established");}
        finally {
            this.lock.readLock().unlock();
        }
    }
    /**
     * Sends an in-game command to the server by invoking the server stub.
     *
     * @param command the {@link GameCommand} to send
     */
    public void sendCommand(GameCommand command){
        this.lock.readLock().lock();
        try {
            this.remote.sendCommand(command);
        }catch (RemoteException e){
            }
        catch (NullPointerException e){System.err.println("Connection not already established");}
        finally {
            this.lock.readLock().unlock();
        }
    }
    /**
     * Pings the server through the stub. The call is synchronous: its successful
     * return is itself the keep-alive acknowledgement, so the pong timestamp is
     * refreshed right after.
     */
    public void ping(){
        this.lock.readLock().lock();
        try {
            this.remote.ping();
            this.updateLastPong();
        }catch (RemoteException e){
        }
        catch (NullPointerException e){System.err.println("Connection not already established");}
        finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Receives a message (remotely invoked by the server on this client stub) and
     * forwards it to the client controller.
     *
     * @param message the incoming message
     * @throws RemoteException if the remote invocation fails
     */
    public void sendMessage(Message message) throws RemoteException{
        this.messageReceiver.receiveMessage(message);
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
     * Tears the connection down exactly once: stops the heartbeat, unexports this
     * client stub and notifies the connection user (triggering reconnection).
     */
    public void disconnect(){
        this.lock.writeLock().lock();
        try {
            if (this.connected.compareAndSet(true,false)){
                this.heartBeat.stop();
                try {
                    UnicastRemoteObject.unexportObject(this, true);
                } catch (NoSuchObjectException e) {e.printStackTrace();}
                this.connectionUser.notifyDisconnection();
            }
        }finally {
            this.lock.writeLock().unlock();
        }
    }


}
