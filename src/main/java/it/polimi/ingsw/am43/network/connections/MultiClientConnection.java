package it.polimi.ingsw.am43.network.connections;

import java.util.UUID;

public interface MultiClientConnection{
    ClientConnection getConnection(UUID id);
}
