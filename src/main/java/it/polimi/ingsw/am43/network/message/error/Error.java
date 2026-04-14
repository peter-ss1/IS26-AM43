package it.polimi.ingsw.am43.network.message.error;

import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.client.ClientModel;
import it.polimi.ingsw.am43.network.message.Message;

public abstract class Error extends Message {
    @Override
    public void execute(ClientModel model) {}
    @Override
    public void execute(ClientController controller) {
        controller.showError(this);
    }

    public static class IllegalMoveError extends Error {
        private String message;
        public IllegalMoveError(String message) {
            this.message = message;
        }
    }
}
