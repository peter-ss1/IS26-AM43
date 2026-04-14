package it.polimi.ingsw.am43.network;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface VirtualServer{
    public void sendCommand(Command command);
}
