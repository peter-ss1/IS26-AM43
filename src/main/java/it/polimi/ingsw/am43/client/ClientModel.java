package it.polimi.ingsw.am43.client;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.network.message.update.Update;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientModel {
    private final Map<Integer, Integer> lobbies;
    private final int lobbyId;
    private final List<Color> availableColors;
    private final ClientPlayer ownPlayer;
    private final List<ClientPlayer> players;
    private final List<Integer> topRowCards;
    private final List<Integer> bottomRowCards;
    private final List<Color> orderQueue;
    private final List<Optional<Color>> offerTrack;
    private int currentEra;

    public ClientModel(Map<Integer, Integer> lobbies, int lobbyId, List<Color> availableColors, ClientPlayer ownPlayer, List<ClientPlayer> players, List<Integer> topRowCards, List<Integer> bottomRowCards, List<Color> orderQueue, List<Optional<Color>> offerTrack, int currentEra) {
        this.lobbies = lobbies;
        this.lobbyId = lobbyId;
        this.availableColors = availableColors;
        this.ownPlayer = ownPlayer;
        this.players = players;
        this.topRowCards = topRowCards;
        this.bottomRowCards = bottomRowCards;
        this.orderQueue = orderQueue;
        this.offerTrack = offerTrack;
        this.currentEra = currentEra;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public List<Color> getAvailableColors() {
        return availableColors;
    }

    public ClientPlayer getOwnPlayer() {
        return ownPlayer;
    }

    public List<ClientPlayer> getPlayers() {
        return players;
    }

    public List<Integer> getTopRowCards() {
        return topRowCards;
    }

    public List<Integer> getBottomRowCards() {
        return bottomRowCards;
    }

    public List<Color> getOrderQueue() {
        return orderQueue;
    }

    public List<Optional<Color>> getOfferTrack() {
        return offerTrack;
    }

    public int getCurrentEra() {
        return currentEra;
    }

    public void setCurrentEra(int currentEra) {
        this.currentEra = currentEra;
    }

    public Map<Integer, Integer> getLobbies() {
        return lobbies;
    }

    public void addLobbies(Map<Integer, Integer> lobbies) {
        this.lobbies.putAll(lobbies);
    }

    public void update(Update update) {

    }
}
