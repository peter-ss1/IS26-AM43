package it.polimi.ingsw.am43.network.message.update;

import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.network.message.Message;

import java.util.Map;

public abstract class Update extends Message {
    @Override
    public void execute(ClientController controller) {}

    public static class AvailableLobbiesUpdate extends Update {
        private final Map<Integer, Integer> lobbies;
        public AvailableLobbiesUpdate(Map<Integer, Integer> lobbies) {
            this.lobbies = lobbies;
        }

        @Override
        public void execute(ClientModel model) {
            model.addLobbies(this.lobbies);
        }
    }
}
