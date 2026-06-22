package it.polimi.ingsw.am43.network.socket.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.Pong;
import it.polimi.ingsw.am43.network.socket.VirtualClientSocket;
import it.polimi.ingsw.am43.utils.UtilsJSON;

import java.io.PrintWriter;

/**
 * Server-side "mouth" of a client connection: serializes outgoing objects to JSON
 * and writes them, one line at a time, onto the client's socket.
 */
public class SocketClientHandler implements VirtualClientSocket {
    final PrintWriter output;

    /**
     * @param out the writer wrapping the client socket's output stream
     */
    public SocketClientHandler(PrintWriter out){
        this.output = out;
    }

    /**
     * Serializes the message to JSON and writes it (followed by a newline) to the
     * socket.
     *
     * @param message the message to send to the client
     */
    public void sendMessage(Message message) {
        try {
            String jsonMessage = UtilsJSON.mapper.writeValueAsString(message);
            output.println(jsonMessage);
        } catch (JsonProcessingException e) {
            System.err.println("Error while parsing message:" + e.getMessage());
        }
    }

    /** Serializes a {@link Pong} and writes it to the socket, replying to a ping. */
    public void pong(){
        try {
            String jsonPong = UtilsJSON.mapper.writeValueAsString(new Pong());
            this.output.println(jsonPong);
        } catch (JsonProcessingException e) {
            System.err.println("Error while parsing pong object:" + e.getMessage());
        }
    }


}
