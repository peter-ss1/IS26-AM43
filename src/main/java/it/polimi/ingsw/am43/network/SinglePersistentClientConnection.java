package it.polimi.ingsw.am43.network;

public interface SinglePersistentClientConnection extends SingleClientConnection{
    public long getLastPing();
    public void updateLastPing();
    public void notifyDisconnection();
}
