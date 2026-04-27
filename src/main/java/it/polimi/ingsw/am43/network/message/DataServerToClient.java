package it.polimi.ingsw.am43.network.message;

import java.io.Serializable;

public abstract sealed class DataServerToClient implements Serializable permits Message, Pong{
}
