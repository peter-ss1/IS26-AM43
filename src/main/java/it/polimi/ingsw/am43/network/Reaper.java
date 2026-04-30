package it.polimi.ingsw.am43.network;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class Reaper{
    private final MultiPersistentClientConnection connections;
    private volatile boolean active;
    private final Thread loop;

    public Reaper(MultiPersistentClientConnection clientConnection){
        this.connections=clientConnection;
        this.active=false;
        this.loop= new Thread(this::runLoop);
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
        while(this.active){
            for(UUID id : this.connections.getIds()){
                now=System.currentTimeMillis();
                if (now - connections.getLastPing(id) > 10000) {
                    this.connections.disconnect(id);//TODO implement removal logic
                }
            }
            try {
                Thread.sleep(3000);
            }catch (InterruptedException e){
                continue;
            }
        }
    }

}
