package it.polimi.ingsw.am43.network.connections;

import it.polimi.ingsw.am43.network.message.Message;

public interface ClientConnection {
    void sendMessage(Message message);
}
