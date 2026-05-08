package it.polimi.ingsw.am43.client;

import it.polimi.ingsw.am43.network.connections.PersistentServerConnection;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeartBeat{
    private final PersistentServerConnection connection;
    private AtomicBoolean active;
    private final Thread loop;


    public HeartBeat(PersistentServerConnection connection){
        this.connection=connection;
        this.active=new AtomicBoolean(false);
        this.loop=new Thread(this::runLoop);
    }


    public void start(){
        if (this.active.compareAndSet(false,true)){
            this.loop.start();
            System.out.println("heartbeat is starting");
        }
    }
    public void stop(){
        if (this.active.compareAndSet(true,false)){
            this.loop.interrupt();
            System.out.println("heartbeat is stopping");
        }
    }

    private void runLoop(){
        long now;
        try {
            while (this.active.get()) {
                this.connection.ping();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    continue;
                }
                now = System.currentTimeMillis();
                if (now - this.connection.getLastPong() > 8000) {
                    throw new Exception(Long.toString(now - this.connection.getLastPong()));
                }
            }
        }catch (Exception e){
            if (this.active.compareAndSet(true,false)){
                this.connection.disconnect();
                e.printStackTrace();
                System.out.println("heartbeat found a disconnection");
            }
        }
    }
}
