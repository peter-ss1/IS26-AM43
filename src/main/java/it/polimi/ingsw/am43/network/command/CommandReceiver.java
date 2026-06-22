package it.polimi.ingsw.am43.network.command;

/**
 * Sink for commands arriving from clients, implemented by the
 * {@link it.polimi.ingsw.am43.controller.ServerController}. Server-side connections
 * forward every incoming command here, regardless of the transport.
 */
public interface CommandReceiver {
    /**
     * Receives a pre-game / lobby command from a client.
     *
     * @param command the incoming {@link ServerCommand}
     */
    public void receiveCommand(ServerCommand command);

    /**
     * Receives an in-game command from a client.
     *
     * @param command the incoming {@link GameCommand}
     */
    public void receiveCommand(GameCommand command);
}
