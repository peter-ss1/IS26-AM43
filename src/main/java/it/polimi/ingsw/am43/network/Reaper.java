package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.connections.MultiPersistentClientConnection;
import it.polimi.ingsw.am43.network.connections.PersistentClientConnection;

/**
 * Server-side liveness watchdog. It runs on its own thread and periodically scans
 * every active client connection: any connection whose last received ping is older
 * than the timeout is considered dead and gets disconnected. It is the server-side
 * counterpart of the client {@link it.polimi.ingsw.am43.client.HeartBeat}.
 */
public class Reaper{
    private final MultiPersistentClientConnection connections;
    private volatile boolean active;
    private final Thread loop;

    /**
     * Creates a reaper bound to the collection of connections it has to watch.
     *
     * @param clientConnection the iterable set of client connections to monitor
     */
    public Reaper(MultiPersistentClientConnection clientConnection){
        this.connections=clientConnection;
        this.active=false;
        this.loop= new Thread(this::runLoop);
    }

    /** Starts the watchdog thread, unless it is already running. */
    public void start(){
        if (!this.active){
            this.active=true;
            this.loop.start();
        }
    }

    /** Stops the watchdog thread and interrupts its current sleep. */
    public void stop(){
        if (this.active){
            this.active=false;
            this.loop.interrupt();
        }
    }

    /**
     * Watchdog loop: every 3 seconds it iterates over all connections and
     * disconnects the ones that have not pinged within the last 5 seconds.
     */
    private void runLoop(){
        long now;
        while(this.active){
            for(PersistentClientConnection connection : this.connections){
                now=System.currentTimeMillis();
                if (now - connection.getLastPing() > 5000) {
                    connection.disconnect();
                    System.out.println("Server activated reaper protocol");
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
