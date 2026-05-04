package it.polimi.ingsw.am43.network.connections;

public interface PersistentClientConnection extends ClientConnection {
    public long getLastPing();
    public void updateLastPing();
    public void disconnect();
}
