package it.polimi.ingsw.am43.network.connections;

import java.util.UUID;

public interface ConnectionHandler {

    public void disconnect(UUID id);
    public void connect(UUID id, PersistentClientConnection connection);
}
