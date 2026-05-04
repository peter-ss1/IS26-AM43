package it.polimi.ingsw.am43.network.Connections;

import it.polimi.ingsw.am43.network.message.Message;

public interface ClientConnection {
    void sendMessage(Message message);
}
