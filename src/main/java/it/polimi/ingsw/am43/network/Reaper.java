package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.connections.MultiPersistentClientConnection;
import it.polimi.ingsw.am43.network.connections.PersistentClientConnection;

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
            for(PersistentClientConnection connection : this.connections){
                now=System.currentTimeMillis();
                if (now - connection.getLastPing() > 10000) {
                    connection.disconnect();//TODO implement removal logic
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
