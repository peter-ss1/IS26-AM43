package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.message.Message;
import it.polimi.ingsw.am43.network.message.update.Update;
import it.polimi.ingsw.am43.network.message.error.Error;

public interface VirtualClient {
    void sendMessage(Message message);
}
