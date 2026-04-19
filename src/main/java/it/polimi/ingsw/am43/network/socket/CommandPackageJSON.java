package it.polimi.ingsw.am43.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.am43.network.command.Destination;
import it.polimi.ingsw.am43.network.command.GameCommand;
import it.polimi.ingsw.am43.network.command.ServerCommand;

public class CommandPackageJSON {
    private final Destination destination;
    private final JsonNode commandJson;

    @JsonCreator
    public CommandPackageJSON(
            @JsonProperty("destination") Destination destination,
            @JsonProperty("commandJson") JsonNode commandJson) {
        this.destination = destination;
        this.commandJson = commandJson;
    }

    @JsonIgnore
    public CommandPackageJSON(ServerCommand serverCommand){
        this.destination=Destination.SERVER;
        this.commandJson=UtilsJSON.mapper.valueToTree(serverCommand);
    }
    @JsonIgnore
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
