package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.client.HeartBeat;
import it.polimi.ingsw.am43.network.message.MessageReceiver;
import it.polimi.ingsw.am43.network.connections.ServerConnectionUser;
import it.polimi.ingsw.am43.network.connections.PersistentServerConnection;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.message.*;

import java.net.InetAddress;
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

    public void open(){
        this.lock.writeLock().lock();
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
                //System.out.println("unable to establish connection");
                this.connected.set(false);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    continue;
                }
            }
        }
        this.lock.writeLock().unlock();
    }
    public void close(){
        this.heartBeat.stop();
    }
    public void sendCommand(ServerCommand command){
        this.lock.readLock().lock();
        //boolean disconnection=false;
        try {
            this.remote.sendCommand(command);
        }catch (RemoteException e){
            //disconnection=true;
        }
        catch (NullPointerException e){System.out.println("connection not already established");}
        finally {
            this.lock.readLock().unlock();
            //if (disconnection)this.disconnect();
        }
    }
    public void sendCommand(GameCommand command){
        this.lock.readLock().lock();
        //boolean disconnection=false;
        try {
            this.remote.sendCommand(command);
        }catch (RemoteException e){
            //disconnection=true;
            }
        catch (NullPointerException e){System.out.println("connection not already established");}
        finally {
            this.lock.readLock().unlock();
            //if (disconnection)this.disconnect();
        }
    }
    public void ping(){
        this.lock.readLock().lock();
        //boolean disconnection=false;
        try {
            this.remote.ping();
            this.updateLastPong();
        }catch (RemoteException e){
            //disconnection=true;
        }
        catch (NullPointerException e){System.out.println("connection not already established");}
        finally {
            this.lock.readLock().unlock();
            //if (disconnection)this.disconnect();
        }
    }

    public void sendMessage(Message message) throws RemoteException{
        this.messageReceiver.receiveMessage(message);
    }

    public long getLastPong(){
        return this.lastPong;
    }
    public void updateLastPong(){
        this.lastPong=System.currentTimeMillis();
    }
    public void disconnect(){
        this.lock.writeLock().lock();
        if (this.connected.compareAndSet(true,false)){
            this.heartBeat.stop();
            try {
                UnicastRemoteObject.unexportObject(this, true);
            } catch (NoSuchObjectException e) {e.printStackTrace();}
            this.connectionUser.notifyDisconnection();
        }
        this.lock.writeLock().unlock();
    }


}
