package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.message.Message;

public interface VirtualClient {
    void sendMessage(Message message);
}
