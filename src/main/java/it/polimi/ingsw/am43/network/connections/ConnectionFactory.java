package it.polimi.ingsw.am43.network.connections;

import it.polimi.ingsw.am43.network.command.CommandReceiver;
import it.polimi.ingsw.am43.network.rmi.ClientRMIConnection;
import it.polimi.ingsw.am43.network.rmi.VirtualClientRmi;
import it.polimi.ingsw.am43.network.socket.server.SocketClientConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class ConnectionFactory {
    private final CommandReceiver commandReceiver;
    private final ConnectionHandler manager;
    public ConnectionFactory(CommandReceiver commandReceiver, ConnectionHandler manager){
        this.commandReceiver=commandReceiver;
        this.manager=manager;
    }
    public SocketClientConnection createConnection(UUID id, Socket socket, BufferedReader in, PrintWriter out) throws IOException {
        return new SocketClientConnection(id,this.commandReceiver,this.manager,socket,in,out);
    }
    public ClientRMIConnection createConnection(UUID id, VirtualClientRmi clientRmi){
        return new ClientRMIConnection(id,this.commandReceiver,this.manager,clientRmi);
    }
}
