package it.polimi.ingsw.am43.network.message;

import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.client.ClientModel;

import java.io.Serializable;

public abstract class Message implements Serializable {
    public abstract void execute(ClientController controller);
    public abstract void execute(ClientModel model);
}
