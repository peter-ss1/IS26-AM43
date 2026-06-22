package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.network.command.DataClientToServer;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.utils.UtilsJSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Server-side "ear" of a client connection: a dedicated thread that blocks on
 * {@code readLine()}, deserializes each incoming JSON line into a
 * {@link DataClientToServer} and routes it back to the owning connection (a ping
 * refreshes liveness, a command is forwarded). It holds a reference to the
 * connection precisely so it can call back into it once it reads something.
 */
public class SocketClientListener {
    final SocketClientConnection connection;
    final BufferedReader input;
    final Thread loop;
    private AtomicBoolean active;

    /**
     * @param connection the connection to call back when data is read
     * @param in         the reader on the client socket's input stream
     */
    public SocketClientListener(SocketClientConnection connection, BufferedReader in){
        this.connection=connection;
        this.input = in;
        this.loop= new Thread(this::runLoop);
        this.active=new AtomicBoolean(false);
    }

    /** Starts the listening thread, unless already running. */
    public void start(){
        if (this.active.compareAndSet(false,true))
            this.loop.start();
    }


    /** Stops the listening thread and interrupts its current blocking read. */
    public void stop(){
        if (this.active.compareAndSet(true,false))
            this.loop.interrupt();
    }

    /**
     * Read loop: turns each incoming JSON line into an object and dispatches it
     * by type. A malformed line is logged and skipped; an I/O failure ends the
     * loop.
     */
    public void runLoop(){
        if(!this.active.get())return;
        String inputData;
        DataClientToServer data;
        try{
            while ((inputData = this.input.readLine()) != null && this.active.get()) {
                try {
                    data = UtilsJSON.mapper.readValue(inputData, DataClientToServer.class);
                    switch (data) {
                        case Ping ping:
                            this.connection.ping();
                            break;
                        case GameCommand gameCommand:
                            this.connection.sendCommand(gameCommand);
                            break;
                        case ServerCommand serverCommand:
                            this.connection.sendCommand(serverCommand);
                            break;
                    }
                } catch (JsonProcessingException e) {
                    System.err.println("Error while parsing command:" + e.getMessage());
                }
            }
        }catch (IOException e){
            if (this.active.compareAndSet(true,false)){
                //this.connection.disconnect();
            }
        }

    }

}
