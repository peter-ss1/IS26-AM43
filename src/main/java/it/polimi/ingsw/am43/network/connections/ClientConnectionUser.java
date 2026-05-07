package it.polimi.ingsw.am43.network.connections;

import java.util.UUID;

public interface ClientConnectionUser {

    public void notifyConnection(UUID id);
    public void notifyDisconnection(UUID id);

}
