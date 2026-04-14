package it.polimi.ingsw.am43.network;

import it.polimi.ingsw.am43.network.command.Command;

public interface VirtualServer {
    public void connect();
    public void sendCommand(Command command);
}
