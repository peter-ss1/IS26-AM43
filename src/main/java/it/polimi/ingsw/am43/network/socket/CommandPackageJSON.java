package it.polimi.ingsw.am43.network.socket;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.am43.network.command.Command;
import it.polimi.ingsw.am43.network.command.Destination;
import it.polimi.ingsw.am43.network.command.game.GameCommand;
import it.polimi.ingsw.am43.network.command.server.ServerCommand;

public class CommandPackageJSON {
    private final Destination destination;
    private final JsonNode commandJson;

    public CommandPackageJSON(ServerCommand serverCommand){
        this.destination=Destination.SERVER;
        this.commandJson=UtilsJSON.mapper.valueToTree(serverCommand);
    }

    public CommandPackageJSON(GameCommand gameCommand){
        this.destination=Destination.GAME;
        this.commandJson=UtilsJSON.mapper.valueToTree(gameCommand);
    }


    public Destination getDestination() {
        return this.destination;
    }

    public JsonNode getCommandJson() {
        return this.commandJson;
    }
}
