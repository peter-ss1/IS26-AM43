package it.polimi.ingsw.am43.network;

public interface PersistentClientConnection extends ClientConnection{
    public long getLastPing();
    public void updateLastPing();
    public void notifyDisconnection();
}
