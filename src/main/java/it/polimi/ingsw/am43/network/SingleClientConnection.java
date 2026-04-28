package it.polimi.ingsw.am43.network;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public interface SingleClientConnection {
    public VirtualClient getRemote();
    public void disconnect();
}
