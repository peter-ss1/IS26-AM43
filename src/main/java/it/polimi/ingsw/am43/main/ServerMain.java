package it.polimi.ingsw.am43.main;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.database.DatabaseConfig;
import it.polimi.ingsw.am43.database.DatabaseManager;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.rmi.ServerRMI;
import it.polimi.ingsw.am43.network.rmi.VirtualServerRMI;
import it.polimi.ingsw.am43.network.socket.server.SocketServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;

public class ServerMain {
    private static final int SOCKET_PORT = 8080;
    private static final int RMI_PORT = 1099;

    static void main() {
        ServerController controller = new ServerController();
        try {

            ObjectMapper mapper = new ObjectMapper();
            File configFile = new File("src/main/resources/it/polimi/ingsw/am43/config.json");

            JsonNode rootNode = mapper.readTree(configFile);
            JsonNode dbNode = rootNode.get("database");

            DatabaseConfig config = mapper.treeToValue(dbNode, DatabaseConfig.class);

             //accende la Connection Pool
            DatabaseManager.initialize(config);


            Connection testConn = DatabaseManager.getConnection();
            if (testConn != null) {
                System.out.println(" SUCCESSO! Il server Java è collegato a MySQL!");
                testConn.close();
            }

        }
        catch (Exception e) {
            System.err.println(" C'è stato un problema durante la connessione:");
            e.printStackTrace();
        }



        try {
            System.out.println("Server IP Address: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("Server IP Address not found");
        }

        new Thread(() -> {
            try {
                SocketServer socketServer = new SocketServer(SOCKET_PORT, controller);
                System.out.println("Server activated socket protocol on port " + SOCKET_PORT);
                socketServer.runServer();
            } catch (IOException e) {
                System.err.println("Socket protocol failed: " + e.getMessage());
            }
        }).start();

        try {
            ServerRMI serverRMI = new ServerRMI(controller);
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(RMI_PORT);
            } catch (RemoteException e) {
                registry = LocateRegistry.getRegistry(RMI_PORT);
            }
            registry.rebind("MesosServer", serverRMI);

            System.out.println("Server activated RMI protocol on port " + RMI_PORT);
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
