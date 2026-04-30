package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.message.Message;

import java.util.UUID;

public interface MultiVirtualClient {
    public void sendMessage(UUID id, Message message);
}
