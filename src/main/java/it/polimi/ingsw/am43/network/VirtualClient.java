package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.message.update.Update;
import it.polimi.ingsw.am43.network.message.error.Error;

public interface VirtualClient {
    void sendMessage(Update update);
    void sendMessage(Error error);
}
