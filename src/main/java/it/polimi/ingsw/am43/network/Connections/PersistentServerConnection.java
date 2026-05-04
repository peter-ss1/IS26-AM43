package it.polimi.ingsw.am43.network.Connections;

public interface PersistentServerConnection extends ServerConnection {
    long getLastPong();
    void updateLastPong();
    void ping();
    void disconnect();
}
