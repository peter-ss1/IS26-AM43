package it.polimi.ingsw.am43.network.command;

/**
 * Sink for in-game commands only, implemented by whoever dispatches
 * {@link GameCommand}s to a specific game (the
 * {@link it.polimi.ingsw.am43.controller.GameController}).
 */
public interface GameCommandReceiver {
    /**
     * Receives an in-game command.
     *
     * @param command the incoming {@link GameCommand}
     */
    public void receiveCommand(GameCommand command);
}
