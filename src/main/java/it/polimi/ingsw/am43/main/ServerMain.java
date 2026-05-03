package it.polimi.ingsw.am43.main;

import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.Connections.ClientsConnectionManager;
import it.polimi.ingsw.am43.network.Connections.ConnectionFactory;
import it.polimi.ingsw.am43.network.rmi.ServerAccessRMI;
import it.polimi.ingsw.am43.network.socket.server.SocketServerAccess;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    private static final int SOCKET_PORT = 8080;
    private static final int RMI_PORT = 1099;

    static void main() {

        ClientsConnectionManager connectionManager= new ClientsConnectionManager();
        ServerController controller = new ServerController(connectionManager);
        connectionManager.setConnectionUser(controller);

        ConnectionFactory connectionFactory= new ConnectionFactory(controller,connectionManager);

        try {
            System.out.println("Server IP Address: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("Server IP Address not found");
        }

        new Thread(() -> {
            try {
                SocketServerAccess socketServer = new SocketServerAccess(SOCKET_PORT,connectionFactory);
                System.out.println("Server activated socket protocol on port " + SOCKET_PORT);
                socketServer.runServer();
            } catch (IOException e) {
                System.err.println("Socket protocol failed: " + e.getMessage());
            }
        }).start();

        try {
            ServerAccessRMI serverAccessRMI = new ServerAccessRMI(connectionFactory);
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(RMI_PORT);
            } catch (RemoteException e) {
                registry = LocateRegistry.getRegistry(RMI_PORT);
            }
            registry.rebind("MesosServer", serverAccessRMI);

            System.out.println("Server activated RMI protocol on port " + RMI_PORT);
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
