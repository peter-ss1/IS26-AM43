package it.polimi.ingsw.am43.network.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.LobbyInfo;
import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.controller.ClientController;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;

import java.util.List;
import java.util.Map;

@JsonSubTypes({
        @JsonSubTypes.Type(value = Update.AvailableLobbiesUpdate.class, name = "showAvailableLobbies"),
        @JsonSubTypes.Type(value = Update.LobbyCreatedUpdate.class, name = "lobbyCreatedUpdate"),
        @JsonSubTypes.Type(value = Update.LobbyJoinedUpdate.class, name = "lobbyJoinedUpdate"),
        @JsonSubTypes.Type(value = Update.NewLobbyUpdate.class, name = "newLobbyUpdate"),
        @JsonSubTypes.Type(value = Update.GameJoinedUpdate.class, name = "gameJoinedUpdate"),
        @JsonSubTypes.Type(value = Update.PlayerAddedUpdate.class, name = "playerAddedUpdate"),
        @JsonSubTypes.Type(value = Update.GameStartedUpdate.class, name = "gameStartedUpdate"),
        @JsonSubTypes.Type(value = Update.NewPhaseUpdate.class, name = "newPhaseUpdate"),
        @JsonSubTypes.Type(value = Update.CurrentPlayerUpdate.class, name = "currentPlayerUpdate"),
        @JsonSubTypes.Type(value = Update.TotemPlacedUpdate.class, name = "totemPlacedUpdate"),
        @JsonSubTypes.Type(value = Update.CardPickedUpdate.class, name = "cardPickedUpdate"),
        @JsonSubTypes.Type(value = Update.TurnEndedUpdate.class, name = "turnEndedUpdate"),
        @JsonSubTypes.Type(value = Update.BuildingBoughtUpdate.class, name = "buildingBoughtUpdate"),
        @JsonSubTypes.Type(value = Update.BuildingEffectUpdate.class, name = "buildingEffectUpdate"),
        @JsonSubTypes.Type(value = Update.HunterEffectUpdate.class, name = "hunterEffectUpdate"),
        @JsonSubTypes.Type(value = Update.HuntEventEffectUpdate.class, name = "huntEventEffectUpdate"),
        @JsonSubTypes.Type(value = Update.PaintingEventEffectUpdate.class, name = "paintingEventEffectUpdate"),
        @JsonSubTypes.Type(value = Update.SustenaceEventEffectUpdate.class, name = "sustenanceEventEffectUpdate"),
        @JsonSubTypes.Type(value = Update.RitualEventEffectUpdate.class, name = "ritualEventEffectUpdate"),
        @JsonSubTypes.Type(value = Update.GameOverUpdate.class, name = "gameOverUpdate"),
        @JsonSubTypes.Type(value = Update.NewRoundUpdate.class, name = "newRoundUpdate"),
        @JsonSubTypes.Type(value = Update.NewLobbyJoinUpdate.class, name = "newLobbyJoinUpdate"),
        @JsonSubTypes.Type(value = Update.OrderModifierUpdate.class, name = "orderModifierUpdate"),
        @JsonSubTypes.Type(value = Update.FoodOfferUpdate.class, name = "foodOfferUpdate"),
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
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("cardId")
        private final int cardId;

        public CardPickedUpdate(@JsonProperty("nickname") String nickname, @JsonProperty("cardId") int cardId) {
            this.nickname = nickname;
            this.cardId = cardId;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().pickCard(this.nickname, this.cardId);
        }
    }

    public static class TotemPlacedUpdate extends Update {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("position")
        private final int position;

        public TotemPlacedUpdate(@JsonProperty("nickname") String nickname, @JsonProperty("position") int position) {
            this.nickname = nickname;
            this.position = position;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().placeTotem(nickname, position);
        }
    }

    public static class TurnEndedUpdate extends Update {
        @JsonProperty("nickname")
        private final String nickname;

        public TurnEndedUpdate(@JsonProperty("nickname") String nickname) {
            this.nickname = nickname;
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
        @JsonProperty("nickname")
        private final String nickname;

        public CurrentPlayerUpdate(@JsonProperty("nickname") String nickname) {
            this.nickname = nickname;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().setCurrentPlayer(this.nickname);
        }
    }

    public static class BuildingBoughtUpdate extends Update {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("cost")
        private final int cost;

        public BuildingBoughtUpdate(@JsonProperty("nickname") String nickname, @JsonProperty("cost") int cost) {
            this.nickname = nickname;
            this.cost = cost;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().buyBuilding(this.nickname, this.cost);
        }
    }

    public static class HunterEffectUpdate extends Update {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("food")
        private final int food;

        public HunterEffectUpdate(@JsonProperty("nickname") String nickname, @JsonProperty("food") int food) {
            this.nickname = nickname;
            this.food = food;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().hunterEffect(this.nickname, this.food);
        }
    }

    public static class BuildingEffectUpdate extends Update {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("bonus")
        private final int bonus;
        @JsonProperty("resource")
        private final String resource;

        public BuildingEffectUpdate(@JsonProperty("nickname") String nickname, @JsonProperty("bonus") int bonus, @JsonProperty("resource") String resource) {
            this.nickname = nickname;
            this.bonus = bonus;
            this.resource = resource;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().applyBuildingEffect(nickname, bonus, resource);
        }
    }

    public static class HuntEventEffectUpdate extends Update {
        @JsonProperty("effects")
        private final Map<String, List<Integer>> effects;

        public HuntEventEffectUpdate(@JsonProperty("effects") Map<String, List<Integer>> effects) {
            this.effects = effects;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().applyHuntEventEffect(effects);
        }
    }

    public static class PaintingEventEffectUpdate extends Update {
        @JsonProperty("effects")
        private final Map<String, Integer> effects;

        public PaintingEventEffectUpdate(@JsonProperty("effects") Map<String, Integer> effects) {
            this.effects = effects;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().applyPaintingEvent(effects);
        }
    }

    public static class SustenaceEventEffectUpdate extends Update {
        @JsonProperty("effects")
        private final Map<String, List<Integer>> effects;

        public SustenaceEventEffectUpdate(@JsonProperty("effects") Map<String, List<Integer>> effects) {
            this.effects = effects;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().applySustenanceEventEffect(effects);
        }
    }

    public static class RitualEventEffectUpdate extends Update {
        @JsonProperty("effects")
        private final Map<String, Integer> effects;

        public RitualEventEffectUpdate(@JsonProperty("effects") Map<String, Integer> effects) {
            this.effects = effects;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().applyRitualEvent(effects);
        }
    }

    public static class NewRoundUpdate extends Update {
        @JsonProperty("currEra")
        private final int currEra;
        @JsonProperty("topRow")
        private final List<Integer> topRow;
        @JsonProperty("bottomRow")
        private final List<Integer> bottomRow;

        public NewRoundUpdate(@JsonProperty("currEra") int currEra, @JsonProperty("topRow") List<Integer> topRowIds, @JsonProperty("bottomRow") List<Integer> bottomRowIds) {
            this.currEra = currEra;
            this.topRow = topRowIds;
            this.bottomRow = bottomRowIds;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().endRound(this.currEra, this.topRow, this.bottomRow);
        }
    }

    public static class GameOverUpdate extends Update {
        @JsonProperty("winners")
        private final List<String> winners;

        public GameOverUpdate(@JsonProperty("winners") List<String> winners) {
            this.winners = winners;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().endGame(winners);
        }
    }

    public static class NewLobbyJoinUpdate extends Update {
        @JsonProperty("currentPlayers")
        private final int currentPlayers;

        public NewLobbyJoinUpdate(@JsonProperty("currentPlayers") int currentPlayers) {
            this.currentPlayers = currentPlayers;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().getOwnLobby().setCurrentPlayers(this.currentPlayers);
        }
    }

    public static class OrderModifierUpdate extends Update {
        @JsonProperty("nickname")
        private final String nickname;
        @JsonProperty("modifier")
        private final int modifier;
        @JsonProperty("prestige")
        private final boolean prestige;

        public OrderModifierUpdate(@JsonProperty("nickname") String nickname,@JsonProperty("modifier") int modifier,@JsonProperty("prestige") boolean prestige) {
            this.nickname = nickname;
            this.modifier = modifier;
            this.prestige = prestige;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().applyModifier(this.nickname, this.modifier, this.prestige);
        }
    }

    public static class FoodOfferUpdate extends Update {
        @JsonProperty("nickname")
        private final String nickname;

        public FoodOfferUpdate(@JsonProperty("nickname") String nickname) {
            this.nickname = nickname;
        }

        @Override
        public void execute(ClientController controller) {
            controller.getLocalModel().resolveFoodOffer(this.nickname);
        }
    }
}