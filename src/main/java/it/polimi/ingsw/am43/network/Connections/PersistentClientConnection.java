package it.polimi.ingsw.am43.network.Connections;

public interface PersistentClientConnection extends ClientConnection {
    public long getLastPing();
    public void updateLastPing();
    public void disconnect();
}
