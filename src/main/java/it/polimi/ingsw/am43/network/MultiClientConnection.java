package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.controller.ClientInfo;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public interface MultiClientConnection {
    public VirtualClient getRemote(UUID id);
    public void register(UUID id, SinglePersistentClientConnection connection);
    public void disconnect(UUID id);
    public boolean isRegistered(UUID id);
}
