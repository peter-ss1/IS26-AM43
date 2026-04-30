package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.rmi.VirtualClientRmi;
import it.polimi.ingsw.am43.network.socket.VirtualClientSocket;

import java.util.Set;
import java.util.UUID;

public interface MultiClientConnection<T extends ClientConnection> extends MultiVirtualClient{

    public ClientConnection getConnection(UUID id);
    public void register(UUID id, ServerBidirectionalConnection connection);
    public void disconnect(UUID id);
    public boolean isRegistered(UUID id);
    public Set<UUID> getIds();
}
