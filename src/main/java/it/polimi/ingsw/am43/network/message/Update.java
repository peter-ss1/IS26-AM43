package it.polimi.ingsw.am43.network.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import it.polimi.ingsw.am43.client.ClientModel;
import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@JsonSubTypes({
        @JsonSubTypes.Type(value = Update.AvailableLobbiesUpdate.class, name = "showAvailableLobbies"),
        @JsonSubTypes.Type(value = Update.LobbyCreatedUpdate.class, name = "lobbyCreatedUpdate"),
        @JsonSubTypes.Type(value = Update.LobbyJoinedUpdate.class, name = "lobbyJoinedUpdate"),
        @JsonSubTypes.Type(value = Update.NewLobbyUpdate.class, name = "newLobbyUpdate"),
        @JsonSubTypes.Type(value = Update.GameJoinedUpdate.class, name = "gameJoinedUpdate"),
        @JsonSubTypes.Type(value = Update.PlayerAddedUpdate.class, name = "playerAddedUpdate"),
        @JsonSubTypes.Type(value = Update.GameStartedUpdate.class, name = "gameStartedUpdate"),
        @JsonSubTypes.Type(value = Update.NewPhaseUpdate.class, name = "newPhaseUpdate")
})

public abstract class Update extends Message {

    public static class AvailableLobbiesUpdate extends Update {
        @JsonProperty("lobbies")
        private final List<LobbyInfo> lobbies;

        public AvailableLobbiesUpdate(@JsonProperty("lobbies") List<LobbyInfo> lobbies) {
            this.lobbies = lobbies;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().refreshLobbies(this.lobbies);
        }
    }

    public static class LobbyCreatedUpdate extends Update {
        @JsonProperty("lobby")
        private final LobbyInfo lobby;
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("color")
        private final Color color;

        public LobbyCreatedUpdate(@JsonProperty("lobby") LobbyInfo lobbyInfo, @JsonProperty("nickname") String nickname, @JsonProperty("color") Color color) {
            this.lobby = lobbyInfo;
            this.nickname = nickname;
            this.color = color;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().setOwnPlayer(new ClientPlayer(this.nickname, this.color));
            controller.getLocalModel().setOwnLobby(this.lobby);
        }
    }

    public static class LobbyJoinedUpdate extends Update {
        @JsonProperty("lobby")
        private final LobbyInfo lobby;
        @JsonProperty("players")
        private final List<ClientPlayer> players;

        public LobbyJoinedUpdate(@JsonProperty("lobby") LobbyInfo lobby, @JsonProperty("players") List<ClientPlayer> players) {
            this.lobby = lobby;
            this.players = players;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().setOtherPlayers(this.players);
            controller.getLocalModel().setOwnLobby(this.lobby);
        }
    }

    public static class GameJoinedUpdate extends Update {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("color")
        private final Color color;

        public GameJoinedUpdate(@JsonProperty("nickname") String nickname, @JsonProperty("color") Color color) {
            this.nickname = nickname;
            this.color = color;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().setOwnPlayer(new ClientPlayer(nickname, color));
        }
    }

    public static class PlayerAddedUpdate extends Update {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("color")
        private final Color color;

        public PlayerAddedUpdate(@JsonProperty("nickname") String nickname, @JsonProperty("color") Color color) {
            this.nickname = nickname;
            this.color = color;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().addPlayer(nickname, color);
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
        public void execute(ClientController controller) {
            if (controller.getLocalModel().getTopRowCards().contains(cardId)) {
                var top = controller.getLocalModel().getTopRowCards();
                top.remove(Integer.valueOf(cardId));
                controller.getLocalModel().setTopRowCards(top);
            } else if (controller.getLocalModel().getBottomRowCards().contains(cardId)) {
                var bottom = controller.getLocalModel().getBottomRowCards();
                bottom.remove(Integer.valueOf(cardId));
                controller.getLocalModel().setBottomRowCards(bottom);
            }

            var player = controller.getLocalModel().getPlayerByNickname(nickname);
            if (player != null) {
                player.updateTribe(cardId);
            }
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
        public void execute(ClientController controller) {
            controller.getLocalModel().placeTotem(nickname, position);
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
        public void execute(ClientController controller) {
            controller.getLocalModel().endTurn(nickname);
        }
    }

    public static class NewLobbyUpdate extends Message {
        @JsonProperty("lobby")
        private final LobbyInfo lobby;

        public NewLobbyUpdate(@JsonProperty("lobby") LobbyInfo lobbyInfo) {
            this.lobby = lobbyInfo;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().addLobby(lobby);
        }
    }

    public static class GameStartedUpdate extends Update {
        @JsonProperty("initialFood")
        private final Map<String, Integer> initialFood;
        @JsonProperty("currPlayer")
        private final String currentPlayerNickname;
        @JsonProperty("topRow")
        private final List<Integer> topRowCards;
        @JsonProperty("bottomRow")
        private final List<Integer> bottomRowCards;
        @JsonProperty("orderQueue")
        private final List<Color> orderQueue;
        @JsonProperty("offerTrack")
        private final List<OfferTrackElement> offerTrack;

        public GameStartedUpdate(@JsonProperty("initialFood") Map<String, Integer> initialFood, @JsonProperty("currPlayer") String currentPlayerNickname,
                                 @JsonProperty("topRow") List<Integer> topRowCards, @JsonProperty("bottomRow") List<Integer> bottomRowCards,
                                 @JsonProperty("orderQueue") List<Color> orderQueue, @JsonProperty("offerTrack") List<OfferTrackElement> offerTrack) {
            this.initialFood = initialFood;
            this.currentPlayerNickname = currentPlayerNickname;
            this.topRowCards = topRowCards;
            this.bottomRowCards = bottomRowCards;
            this.orderQueue = orderQueue;
            this.offerTrack = offerTrack;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().startGame(initialFood, currentPlayerNickname, topRowCards, bottomRowCards, orderQueue, offerTrack);
        }
    }

    public static class NewPhaseUpdate extends Update {
        @JsonProperty("phase")
        private final GamePhase phase;

        public NewPhaseUpdate(@JsonProperty("phase") GamePhase phase) {
            this.phase = phase;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().setPhase(phase);
        }
    }

    public static class CurrentPlayerUpdate extends Update {
        private final String nickname;

        public CurrentPlayerUpdate(String nickname) {
            this.nickname = nickname;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().setCurrentPlayer(this.nickname);
        }
    }
}