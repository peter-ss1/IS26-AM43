package it.polimi.ingsw.am43.network.socket.client;
import it.polimi.ingsw.am43.network.VirtualServer;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;
import it.polimi.ingsw.am43.network.message.Message;

public interface VirtualServerSocket extends VirtualServer {
    void sendCommand(ServerCommand command);
    void sendCommand(GameCommand command);
}
