package it.polimi.ingsw.am43.network.command;

public interface CommandReceiver {
    public void receiveCommand(ServerCommand command);
    public void receiveCommand(GameCommand command);
}
