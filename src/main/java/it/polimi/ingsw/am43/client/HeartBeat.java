package it.polimi.ingsw.am43.client;

import it.polimi.ingsw.am43.network.Connections.PersistentServerConnection;

public class HeartBeat{
    private final PersistentServerConnection connection;
    private volatile boolean active;
    private Thread loop;


    public HeartBeat(PersistentServerConnection connection){
        this.connection=connection;
        this.active= false;
        this.loop=new Thread(this::runLoop);
    }


    public void start(){
        this.active=true;
        this.loop.start();
    }
    public void stop(){
        this.active=false;
        this.loop.interrupt();
    }

    private void runLoop(){
        long now;
        while (this.active){
            try {
                this.connection.ping();
                Thread.sleep(2000);
                now=System.currentTimeMillis();
                if(now-this.connection.getLastPong()>8000){
                    throw new Exception();
                }
            }catch (Exception e){
                this.active=false;
                this.connection.disconnect();
            }
        }
    }
}
