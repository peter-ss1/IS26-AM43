package it.polimi.ingsw.am43.network.Connections;

import java.util.UUID;

public interface MultiClientConnection{
    ClientConnection getConnection(UUID id);
}
