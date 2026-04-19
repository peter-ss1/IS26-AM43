package it.polimi.ingsw.am43.network.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.command.ServerCommand;

import java.util.List;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "messageType"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = Update.StringUpdate.class, name = "stringUpdate"),
})

public abstract class Update extends Message {
    @Override
    public void execute(ClientController controller) {
    }

    public static class AvailableLobbiesUpdate extends Update {
        private final List<LobbyInfo> lobbies;

        public AvailableLobbiesUpdate(List<LobbyInfo> lobbies) {
            this.lobbies = lobbies;
        }

        public  List<LobbyInfo> getLobbies() {
            return lobbies;
        }

        @Override
        public void execute(ClientModel model) {
            model.clearLobbies();
            model.addLobbies(this.lobbies);
        }
    }

    public static class LobbyCreatedUpdate extends Update {
        private final int lobbyId;
        private final int expectedPlayers;

        public LobbyCreatedUpdate(int lobbyId, int expectedPlayers) {
            this.lobbyId = lobbyId;
            this.expectedPlayers = expectedPlayers;
        }

        public int getLobbyId() {
            return lobbyId;
        }

        public int getExpectedPlayers() {
            return expectedPlayers;
        }

        @Override
        public void execute(ClientModel model) {
            model.setLobbyId(lobbyId);
            model.addLobby(new LobbyInfo(lobbyId, expectedPlayers, 1));
        }
    }

    public static class LobbyJoinedUpdate extends Update {
        private final int lobbyId;
        private final int expectedPlayers;
        private final int currentPlayers;

        public LobbyJoinedUpdate(int lobbyId, int expectedPlayers, int currentPlayers) {
            this.lobbyId = lobbyId;
            this.expectedPlayers = expectedPlayers;
            this.currentPlayers = currentPlayers;
        }

        public int getLobbyId() {
            return lobbyId;
        }

        public int getExpectedPlayers() {
            return expectedPlayers;
        }

        public int getCurrentPlayers() {
            return currentPlayers;
        }

        @Override
        public void execute(ClientModel model) {
            model.setLobbyId(lobbyId);
            model.addLobby(new LobbyInfo(lobbyId, expectedPlayers, currentPlayers));
        }
    }

    public static class PlayerAddedUpdate extends Update {
        private final String nickname;
        private final Color color;

        public PlayerAddedUpdate(String nickname, Color color) {
            this.nickname = nickname;
            this.color = color;
        }

        public String getNickname() {
            return nickname;
        }

        public Color getColor() {
            return color;
        }

        @Override
        public void execute(ClientModel model) {
            model.addOrUpdatePlayer(nickname, color);
        }
    }

    public static class CardPickedUpdate extends Update {
        private final String nickname;
        private final int cardId;

        public CardPickedUpdate(String nickname, int cardId) {
            this.nickname = nickname;
            this.cardId = cardId;
        }

        public String getNickname() {
            return nickname;
        }

        public int getCardId() {
            return cardId;
        }

        @Override
        public void execute(ClientModel model) {
            if (model.getTopRowCards().contains(cardId)) {
                var top = model.getTopRowCards();
                top.remove(Integer.valueOf(cardId));
                model.setTopRowCards(top);
            } else if (model.getBottomRowCards().contains(cardId)) {
                var bottom = model.getBottomRowCards();
                bottom.remove(Integer.valueOf(cardId));
                model.setBottomRowCards(bottom);
            }

            var player = model.getPlayerByNickname(nickname);
            if (player != null) {
                player.updateTribe(cardId);
            }
        }
    }

    public static class GameStartedUpdate extends Update {
        private final int lobbyId;

        public GameStartedUpdate(int lobbyId) {
            this.lobbyId = lobbyId;
        }

        public int getLobbyId() {
            return lobbyId;
        }

        @Override
        public void execute(ClientModel model) {
            model.setLobbyId(lobbyId);
            model.setGameStarted(true);
        }
    }

    public static class TotemPlacedUpdate extends Update {
        private final String nickname;
        private final int position;

        public TotemPlacedUpdate(String nickname, int position) {
            this.nickname = nickname;
            this.position = position;
        }

        public String getNickname() {
            return nickname;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public void execute(ClientModel model) {
            model.placeTotem(nickname, position);
        }
    }

    public static class TurnEndedUpdate extends Update {
        private final String nickname;

        public TurnEndedUpdate(String nickname) {
            this.nickname = nickname;
        }

        public String getNickname() {
            return nickname;
        }

        @Override
        public void execute(ClientModel model) {
            model.endTurn(nickname);
        }
    }

    public static class PlayerIdentityUpdate extends Update {
        private final UUID playerId;

        public PlayerIdentityUpdate(UUID playerId) {
            this.playerId = playerId;
        }

        public UUID getPlayerId() {
            return playerId;
        }

        @Override
        public void execute(ClientController controller) {
            controller.setPlayerId(playerId);
        }

        @Override
        public void execute(ClientModel model) {
        }
    }

    public static class StringUpdate extends Update {

        @JsonProperty("message")
        private final String message;
        public StringUpdate(@JsonProperty("message")String message) {
            super();
            this.message = message;
        }

        @Override
        public void execute(ClientController controller) {
            controller.showString(message);
        }

        @Override
        public void execute(ClientModel model) {

        }
    }
}