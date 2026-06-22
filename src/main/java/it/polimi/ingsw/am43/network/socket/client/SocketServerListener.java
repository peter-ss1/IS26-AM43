package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.network.message.DataServerToClient;
import it.polimi.ingsw.am43.network.message.Error;
import it.polimi.ingsw.am43.network.message.Pong;
import it.polimi.ingsw.am43.network.message.Update;
import it.polimi.ingsw.am43.utils.UtilsJSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Client-side "ear" of the server connection: a dedicated thread that blocks on
 * {@code readLine()}, deserializes each incoming JSON line into a
 * {@link DataServerToClient} and routes it to the owning connection (a pong
 * refreshes liveness, an update or error is forwarded). It is the mirror image of
 * the server-side {@code SocketClientListener}.
 */
public class SocketServerListener {
    private final BufferedReader input;
    private final Thread loop;
    private AtomicBoolean active;
    private final SocketServerConnection connection;

    /**
     * @param connection the connection to call back when data is read
     * @param in         the reader on the server socket's input stream
     * @throws IOException if the listener cannot be set up
     */
    public SocketServerListener(SocketServerConnection connection, BufferedReader in) throws IOException{
        this.input=in;
        this.connection=connection;
        this.loop=new Thread(this::runLoop);
        this.active=new AtomicBoolean(false);
    }

    /** Starts the listening thread, unless already running. */
    public void start(){
        if (this.active.compareAndSet(false,true))
            this.loop.start();
    }

    /** Stops the listening thread and interrupts its current blocking read. */
    public void stop(){
        if(this.active.compareAndSet(true,false))
            this.loop.interrupt();
    }

    /**
     * Read loop: turns each incoming JSON line into an object and dispatches it
     * by type. A malformed line is skipped; an I/O failure ends the loop.
     */
    private void runLoop(){
        String json;
        DataServerToClient data;
        try{
            while ((json = input.readLine()) != null && this.active.get()) {
                try {
                    data = UtilsJSON.mapper.readValue(json, DataServerToClient.class);
                    switch (data) {
                        case Pong pong:
                            this.connection.pong();
                            break;
                        case Update update:
                            this.connection.sendMessage(update);
                            break;
                        case Error error:
                            this.connection.sendMessage(error);
                            break;
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            if (this.active.compareAndSet(true,false)){
            }
        }
    }
}
