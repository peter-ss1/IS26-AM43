package it.polimi.ingsw.am43.main;

import it.polimi.ingsw.am43.controller.PersistencyManager;
import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.am43.controller.ServerController;
import it.polimi.ingsw.am43.network.connections.ClientsConnectionManager;
import it.polimi.ingsw.am43.network.connections.ConnectionFactory;
import it.polimi.ingsw.am43.network.rmi.ServerAccessRMI;
import it.polimi.ingsw.am43.network.socket.server.SocketServerAccess;
import it.polimi.ingsw.am43.database.DatabaseConfig;
import it.polimi.ingsw.am43.database.DatabaseManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;

public class ServerMain {
    private static final int SOCKET_PORT = 8081;
    private static final int RMI_PORT = 1099;

    static void main() {
        System.setProperty("org.slf4j.simpleLogger.log.com.zaxxer.hikari", "warn");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");

        ClientsConnectionManager connectionManager= new ClientsConnectionManager();
        ServerController controller = new ServerController(connectionManager);
        connectionManager.setConnectionUser(controller);
        ConnectionFactory connectionFactory= new ConnectionFactory(controller,connectionManager);

        PersistencyManager.loadSavedStatus(controller);
        try {

            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = ServerMain.class.getResourceAsStream("/it/polimi/ingsw/am43/config.json");
            JsonNode rootNode = mapper.readTree(inputStream);
            JsonNode dbNode = rootNode.get("database");

            DatabaseConfig config = mapper.treeToValue(dbNode, DatabaseConfig.class);

             //accende la Connection Pool
            DatabaseManager.initialize(config);


            Connection testConn = DatabaseManager.getConnection();
            if (testConn != null) {
                System.out.println("Database connection established");
                testConn.close();
            }

        }
        catch (Exception e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
        }



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
