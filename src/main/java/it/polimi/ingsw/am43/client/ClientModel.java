package it.polimi.ingsw.am43.client;

import it.polimi.ingsw.am43.model.enums.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientModel {
    private final List<LobbyInfo> lobbies;
    private int lobbyId;
    private final List<Color> availableColors;
    private ClientPlayer ownPlayer;
    private final List<ClientPlayer> players;
    private final List<Integer> topRowCards;
    private final List<Integer> bottomRowCards;
    private final List<Color> orderQueue;
    private final List<Optional<Color>> offerTrack;
    private int currentEra;
    private boolean gameStarted;

    public ClientModel(List<LobbyInfo> lobbies,
                       int lobbyId,
                       List<Color> availableColors,
                       ClientPlayer ownPlayer,
                       List<ClientPlayer> players,
                       List<Integer> topRowCards,
                       List<Integer> bottomRowCards,
                       List<Color> orderQueue,
                       List<Optional<Color>> offerTrack,
                       int currentEra) {
        this.lobbies = (lobbies == null) ? new ArrayList<>() : new ArrayList<>(lobbies);
        this.lobbyId = lobbyId;
        this.availableColors = (availableColors == null) ? new ArrayList<>() : new ArrayList<>(availableColors);
        this.ownPlayer = ownPlayer;
        this.players = (players == null) ? new ArrayList<>() : new ArrayList<>(players);
        this.topRowCards = (topRowCards == null) ? new ArrayList<>() : new ArrayList<>(topRowCards);
        this.bottomRowCards = (bottomRowCards == null) ? new ArrayList<>() : new ArrayList<>(bottomRowCards);
        this.orderQueue = (orderQueue == null) ? new ArrayList<>() : new ArrayList<>(orderQueue);
        this.offerTrack = (offerTrack == null) ? new ArrayList<>() : new ArrayList<>(offerTrack);
        this.currentEra = currentEra;
        this.gameStarted = false;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public List<Color> getAvailableColors() {
        return new ArrayList<>(availableColors);
    }

    public void setAvailableColors(List<Color> colors) {
        this.availableColors.clear();
        if (colors != null) {
            this.availableColors.addAll(colors);
        }
    }

    public ClientPlayer getOwnPlayer() {
        return ownPlayer;
    }

    public void setOwnPlayer(ClientPlayer ownPlayer) {
        this.ownPlayer = ownPlayer;
    }

    public List<ClientPlayer> getPlayers() {
        return new ArrayList<>(players);
    }

    public void setPlayers(List<ClientPlayer> players) {
        this.players.clear();
        if (players != null) {
            this.players.addAll(players);
        }
    }

    public List<Integer> getTopRowCards() {
        return new ArrayList<>(topRowCards);
    }

    public void setTopRowCards(List<Integer> topRowCards) {
        this.topRowCards.clear();
        if (topRowCards != null) {
            this.topRowCards.addAll(topRowCards);
        }
    }

    public List<Integer> getBottomRowCards() {
        return new ArrayList<>(bottomRowCards);
    }

    public void setBottomRowCards(List<Integer> bottomRowCards) {
        this.bottomRowCards.clear();
        if (bottomRowCards != null) {
            this.bottomRowCards.addAll(bottomRowCards);
        }
    }

    public List<Color> getOrderQueue() {
        return new ArrayList<>(orderQueue);
    }

    public void setOrderQueue(List<Color> orderQueue) {
        this.orderQueue.clear();
        if (orderQueue != null) {
            this.orderQueue.addAll(orderQueue);
        }
    }

    public List<Optional<Color>> getOfferTrack() {
        return new ArrayList<>(offerTrack);
    }

    public void setOfferTrack(List<Optional<Color>> offerTrack) {
        this.offerTrack.clear();
        if (offerTrack != null) {
            this.offerTrack.addAll(offerTrack);
        }
    }

    public int getCurrentEra() {
        return currentEra;
    }

    public void setCurrentEra(int currentEra) {
        this.currentEra = currentEra;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public List<LobbyInfo> getLobbies() {
        return new ArrayList<>(lobbies);
    }

    public void addLobbies(List<LobbyInfo> lobbies) {
        if (lobbies != null) {
            this.lobbies.addAll(lobbies);
        }
    }

    public void clearLobbies() {
        this.lobbies.clear();
    }

    public boolean containsPlayer(String nickname) {
        return this.players.stream().anyMatch(player -> player.getNickname().equals(nickname));
    }

    public ClientPlayer getPlayerByNickname(String nickname) {
        return this.players.stream()
                .filter(player -> player.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);
    }

    public void addOrUpdatePlayer(String nickname, Color color) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player == null) {
            this.players.add(new ClientPlayer(nickname, color, 0, 0, new ArrayList<>()));
        } else {
            player.setColor(color);
        }
    }

    public void placeTotem(String nickname, int position) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player == null) {
            return;
        }

        Color color = player.getColor();

        while (offerTrack.size() <= position) {
            offerTrack.add(Optional.empty());
        }

        offerTrack.set(position, Optional.ofNullable(color));
        orderQueue.remove(color);
    }

    public void endTurn(String nickname) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player == null || player.getColor() == null) {
            return;
        }

        Color color = player.getColor();

        for (int i = 0; i < offerTrack.size(); i++) {
            Optional<Color> slot = offerTrack.get(i);
            if (slot.isPresent() && slot.get().equals(color)) {
                offerTrack.set(i, Optional.empty());
                break;
            }
        }

        orderQueue.add(color);
    }

    public void addLobby(LobbyInfo lobbyInfo) {
        this.lobbies.add(lobbyInfo);
    }
}