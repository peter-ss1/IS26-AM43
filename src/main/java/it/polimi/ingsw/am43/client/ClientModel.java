package it.polimi.ingsw.am43.client;

import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.model.enums.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientModel {
    private List<LobbyInfo> lobbies;
    private LobbyInfo ownLobby;
    private List<Color> availableColors;
    private ClientPlayer ownPlayer;
    private List<ClientPlayer> otherPlayers;
    private List<Integer> topRowCards;
    private List<Integer> bottomRowCards;
    private List<Color> orderQueue;
    private List<Optional<Color>> offerTrack;
    private int currentEra;
    private boolean gameStarted;
    private String currPlayerNickname;

    private UI ui;

    public ClientModel(UI ui) {
        this.lobbies = new ArrayList<>();
        this.ownLobby = new LobbyInfo(0, 0, 0);
        this.availableColors = new ArrayList<>();
        this.ownPlayer = null;
        this.otherPlayers = new ArrayList<>();
        this.topRowCards = new ArrayList<>();
        this.bottomRowCards = new ArrayList<>();
        this.orderQueue = new ArrayList<>();
        this.offerTrack = new ArrayList<>();
        this.currentEra = 0;
        this.gameStarted = false;
        this.ui = ui;
        this.currPlayerNickname = "";
    }

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
        this.ownLobby = new LobbyInfo(lobbyId, 0, 0);
        this.availableColors = (availableColors == null) ? new ArrayList<>() : new ArrayList<>(availableColors);
        this.ownPlayer = ownPlayer;
        this.otherPlayers = (players == null) ? new ArrayList<>() : new ArrayList<>(players);
        this.topRowCards = (topRowCards == null) ? new ArrayList<>() : new ArrayList<>(topRowCards);
        this.bottomRowCards = (bottomRowCards == null) ? new ArrayList<>() : new ArrayList<>(bottomRowCards);
        this.orderQueue = (orderQueue == null) ? new ArrayList<>() : new ArrayList<>(orderQueue);
        this.offerTrack = (offerTrack == null) ? new ArrayList<>() : new ArrayList<>(offerTrack);
        this.currentEra = currentEra;
        this.gameStarted = false;
    }

    public void refreshLobbies(List<LobbyInfo> lobbies) {
        this.lobbies.clear();
        this.lobbies.addAll(lobbies);
        this.ui.lobbyUpdate();
    }

    public LobbyInfo getOwnLobby() {
        return ownLobby;
    }

    public void setOwnLobby(LobbyInfo ownLobby) {
        this.ownLobby = ownLobby;
        ui.enterLobby();
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
        this.ui.showPlayer();
    }

    public List<ClientPlayer> getOtherPlayers() {
        return new ArrayList<>(otherPlayers);
    }

    public void setOtherPlayers(List<ClientPlayer> otherPlayers) {
        this.otherPlayers.clear();
        if (otherPlayers != null) {
            this.otherPlayers.addAll(otherPlayers);
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

    public boolean containsPlayer(String nickname) {
        return this.otherPlayers.stream().anyMatch(player -> player.getNickname().equals(nickname));
    }

    public ClientPlayer getPlayerByNickname(String nickname) {
        return this.otherPlayers.stream()
                .filter(player -> player.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);
    }

    public void addOtherPlayer(String nickname, Color color) {
        if (!this.ownPlayer.getNickname().equals(nickname)) {
            this.otherPlayers.add(new ClientPlayer(nickname, color));
            this.ui.showNewPlayer();
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
        this.lobbies.removeIf(l -> l.getLobbyId() == lobbyInfo.getLobbyId());
        if (lobbyInfo.getNumPlayers() != lobbyInfo.getCurrentPlayers()) {
            this.lobbies.add(lobbyInfo);
        }
        this.ui.lobbyUpdate();
    }

    public void startGame(String firstPlayerNickname, List<Integer> visibleIds) {
        this.gameStarted = true;
        this.currPlayerNickname = firstPlayerNickname;
        this.topRowCards = visibleIds.subList(0, visibleIds.size()/2);
        this.bottomRowCards = visibleIds.subList(visibleIds.size()/2, visibleIds.size()-1);
        this.ui.showStartedGame();
    }

    public String getCurrentPlayerNickname() {
        return this.currPlayerNickname;
    }
}