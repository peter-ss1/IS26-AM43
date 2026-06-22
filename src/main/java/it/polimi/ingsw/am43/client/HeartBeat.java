package it.polimi.ingsw.am43.client;

import it.polimi.ingsw.am43.network.connections.PersistentServerConnection;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The HeartBeat class manages a background connection-monitoring mechanism.
 * It periodically transmits ping requests to the server and verifies that
 * pong responses are received within a defined timeout threshold,
 * automatically disconnecting if the server becomes unresponsive.
 */
public class HeartBeat{
    private final PersistentServerConnection connection;
    private AtomicBoolean active;
    private final Thread loop;

    /**
     * Constructs a new HeartBeat instance associated with the specified connection.
     * The monitoring thread is prepared but not automatically started.
     *
     * @param connection The persistent server connection to monitor and ping.
     */
    public HeartBeat(PersistentServerConnection connection){
        this.connection=connection;
        this.active=new AtomicBoolean(false);
        this.loop=new Thread(this::runLoop);
    }

    /**
     * Starts the background heartbeat monitoring loop if it is not already running.
     * Utilizes atomic validation to guarantee the execution thread is launched exactly once.
     */
    public void start(){
        if (this.active.compareAndSet(false,true)){
            this.loop.start();
        }
    }
    /**
     * Stops the heartbeat monitoring loop if it is currently active and resets the operational flag.
     */
    public void stop(){
        if (this.active.compareAndSet(true,false)){
            this.loop.interrupt();
        }
    }

    /**
     * The core loop executed on the background thread.
     * Periodically sends a ping every 2000 milliseconds and assesses the time elapsed
     * since the last received server pong response. If the latency exceeds 5000 milliseconds,
     * an exception is raised, terminating the loop and triggering a clean disconnection sequence.
     */
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
                if (now - this.connection.getLastPong() > 5000) {
                    throw new Exception(Long.toString(now - this.connection.getLastPong()));
                }
            }
        }catch (Exception e){
            if (this.active.compareAndSet(true,false)){
                this.connection.disconnect();
            }
        }
    }
}
