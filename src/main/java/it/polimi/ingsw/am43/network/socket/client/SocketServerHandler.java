package it.polimi.ingsw.am43.network.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.Ping;
import it.polimi.ingsw.am43.network.command.ServerCommand;
import it.polimi.ingsw.am43.network.socket.VirtualServerSocket;
import it.polimi.ingsw.am43.utils.UtilsJSON;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Client-side "mouth" of the server connection: serializes outgoing commands (and
 * pings) to JSON and writes them, one line at a time, onto the server's socket.
 */
public class SocketServerHandler implements VirtualServerSocket {
    private final PrintWriter output;

    /**
     * @param out the writer wrapping the server socket's output stream
     * @throws IOException if the writer cannot be set up
     */
    public SocketServerHandler(PrintWriter out)throws IOException {
        this.output=out;
    }

    /**
     * Serializes and writes a pre-game / lobby command to the socket.
     *
     * @param serverCommand the {@link ServerCommand} to send
     */
    public void sendCommand(ServerCommand serverCommand){
        try {
            String command = UtilsJSON.mapper.writeValueAsString(serverCommand);
            output.println(command);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

    /**
     * Serializes and writes an in-game command to the socket.
     *
     * @param gameCommand the {@link GameCommand} to send
     */
    public void sendCommand(GameCommand gameCommand){
        try {
            String command = UtilsJSON.mapper.writeValueAsString(gameCommand);
            output.println(command);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }

    /** Serializes a {@link Ping} and writes it to the socket. */
    public void ping(){
        try {
            String pingJson = UtilsJSON.mapper.writeValueAsString(new Ping());
            output.println(pingJson);
        }catch (JsonProcessingException e){throw new RuntimeException(e.getMessage());}
    }
}

