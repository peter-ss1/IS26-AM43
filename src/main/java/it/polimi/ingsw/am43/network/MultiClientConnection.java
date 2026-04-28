package it.polimi.ingsw.am43.network;

import java.util.Set;
import java.util.UUID;

public interface MultiClientConnection{
    public VirtualClient getRemote(UUID id);
    public void register(UUID id, SinglePersistentClientConnection connection);
    public void disconnect(UUID id);
    public boolean isRegistered(UUID id);
    public Set<UUID> getIds();

}
