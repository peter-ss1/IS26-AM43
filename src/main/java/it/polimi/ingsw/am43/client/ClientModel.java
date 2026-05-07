package it.polimi.ingsw.am43.client;

import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;

import java.util.*;

public class ClientModel {
    private LobbyInfo ownLobby;
    private final List<LobbyInfo> lobbies;
    private ClientPlayer ownPlayer;
    private List<ClientPlayer> otherPlayers;
    private final List<Integer> topRowCards;
    private final List<Integer> bottomRowCards;
    private final List<Color> orderQueue;
    private final List<OfferTrackElement> offerTrack;
    private GamePhase phase;
    private int currentEra;
    private String currPlayerNickname;
    private final UI ui;
    private boolean validating;
    private final List<String> winners;

    public ClientModel(UI ui) {
        this.lobbies = new ArrayList<>();
        this.ownLobby = new LobbyInfo(0, 0, 0);
        this.ownPlayer = null;
        this.otherPlayers = new ArrayList<>();
        this.topRowCards = new ArrayList<>();
        this.bottomRowCards = new ArrayList<>();
        this.orderQueue = new ArrayList<>();
        this.offerTrack = new ArrayList<>();
        this.phase = null;
        this.currentEra = 0;
        this.ui = ui;
        this.currPlayerNickname = "";
        this.validating = false;
        this.winners = new ArrayList<>();
    }

    public void refreshLobbies(List<LobbyInfo> lobbies) {
        this.lobbies.clear();
        this.lobbies.addAll(lobbies);
        this.ui.showAvailableLobbies();
    }

    public int getNumPlayers() {
        return otherPlayers.size() + 1;
    }


    public List<OfferTrackElement> getOfferTrack() {
        return new ArrayList<>(this.offerTrack);
    }

    public List<Color> getAvailableColors() {
        return Arrays.stream(Color.values())
                .filter(color -> this.otherPlayers.stream().noneMatch(otherPlayer -> otherPlayer.getColor().equals(color)))
                .toList();
    }

    public LobbyInfo getOwnLobby() {
        return ownLobby;
    }

    public void setOwnLobby(LobbyInfo ownLobby) {
        this.ownLobby = ownLobby;
        ui.enterLobby();
    }

    public ClientPlayer getOwnPlayer() {
        return ownPlayer;
    }

    public void setOwnPlayer(ClientPlayer ownPlayer) {
        this.ownPlayer = ownPlayer;
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

    public void setOfferTrack(List<Optional<Color>> offerTrack) {
        this.offerTrack.clear();
        if (offerTrack != null) {
            //this.offerTrack.addAll(offerTrack);
        }
    }

    public int getCurrentEra() {
        return currentEra;
    }

    public void setCurrentEra(int currentEra) {
        this.currentEra = currentEra;
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
        return this.getAllPlayers().stream()
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
        this.orderQueue.remove(player.getColor());
        this.offerTrack.get(position).setColor(player.getColor());
        this.ui.showTotemPlaced(player.getNickname(), position);
        this.validating = false;
    }

    public void endTurn(String nickname) {
        Color color = getPlayerByNickname(nickname).getColor();
        this.offerTrack.stream().filter(o -> o.getColor() != null && o.getColor().equals(color)).findFirst().ifPresent(o -> {
            o.setColor(null);
        });
        this.orderQueue.add(color);
        this.validating = false;
    }

    public void addLobby(LobbyInfo lobbyInfo) {
        this.lobbies.removeIf(l -> l.getLobbyId() == lobbyInfo.getLobbyId());
        if (lobbyInfo.getNumPlayers() != lobbyInfo.getCurrentPlayers()) {
            this.lobbies.add(lobbyInfo);
        }
        this.ui.showAvailableLobbies();
    }

    public String getCurrentPlayerNickname() {
        return this.currPlayerNickname;
    }

    public boolean isNicknameAvailable(String nickname) {
        return this.otherPlayers.stream().noneMatch(player -> player.getNickname().equals(nickname));
    }

    public boolean isColorAvailable(Color color) {
        return this.otherPlayers.stream().noneMatch(player -> player.getColor().equals(color));
    }

    public List<ClientPlayer> getAllPlayers() {
        List<ClientPlayer> allPlayers = new ArrayList<>();
        if (this.ownPlayer != null) allPlayers.add(this.ownPlayer);
        allPlayers.addAll(this.otherPlayers);
        return allPlayers;
    }

    public void addPlayer(String nickname, Color color) {
        if (this.ownPlayer == null || !this.ownPlayer.getNickname().equals(nickname))
            this.otherPlayers.add(new ClientPlayer(nickname, color));
        this.ui.showNewPlayer();
    }

    public GamePhase getPhase() {
        return this.phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public void setCurrentPlayer(String nickname) {
        this.currPlayerNickname = nickname;
        this.ui.showNewCurrPlayer();
    }

    public void startGame(Map<String, Integer> initialFood, String currentPlayerNickname, List<Integer> topRowCards, List<Integer> bottomRowCards, List<Color> orderQueue, List<OfferTrackElement> offerTrack) {
        this.currentEra = 1;
        this.getAllPlayers().forEach(player -> {
            player.setFood(initialFood.get(player.getNickname()));
        });
        this.currPlayerNickname = currentPlayerNickname;
        this.topRowCards.addAll(topRowCards);
        this.bottomRowCards.addAll(bottomRowCards);
        this.orderQueue.addAll(orderQueue);
        this.offerTrack.addAll(offerTrack);
        this.ui.showGameStart();
    }

    public int getIdByPos(String row, int pos) {
        if (row.equalsIgnoreCase("top")) {
            if (pos < 0 || pos >= this.topRowCards.size()) throw new IllegalArgumentException("Invalid row position");
            return topRowCards.get(pos);
        } else {
            if (pos < 0 || pos >= bottomRowCards.size()) throw new IllegalArgumentException("Invalid row position");
            return bottomRowCards.get(pos);
        }
    }

    public boolean isValidating() {
        return this.validating;
    }

    public boolean isOwnTurn() {
        return this.ownPlayer.getNickname().equals(this.currPlayerNickname);
    }

    public boolean isPlayer(String nickname) {
        return this.getAllPlayers().stream().anyMatch(player -> player.getNickname().equals(nickname));
    }

    public void startValidation() {
        this.validating = true;
    }

    public void stopValidation() {
        this.validating = false;
    }

    public void pickCard(String nickname, int cardId) {
        this.getPlayerByNickname(nickname).updateTribe(cardId);
        this.topRowCards.remove((Integer) cardId);
        this.bottomRowCards.remove((Integer) cardId);
        this.ui.showCardPicked(nickname, cardId);
        this.validating = false;
    }

    public void buyBuilding(String nickname, int cost) {
        this.getPlayerByNickname(nickname).alterFood(-cost);
        this.ui.showBuildingAcquisition(nickname, cost);
    }

    public void hunterEffect(String nickname, int food) {
        this.getPlayerByNickname(nickname).alterFood(food);
        this.ui.showHunterEffect(nickname, food);
    }

    public void applyBuildingEffect(String nickname, int bonus, String resource) {
        if (resource.equalsIgnoreCase("food")) {
            this.getPlayerByNickname(nickname).alterFood(bonus);
        }
        if (resource.equalsIgnoreCase("prestige points")) {
            this.getPlayerByNickname(nickname).alterPrestigePoints(bonus);
        }
        this.ui.showBuildingEffect(nickname, bonus, resource);
    }

    public void applyHuntEventEffect(Map<String, List<Integer>> effects) {
        effects.forEach((key, value) -> {
            this.getPlayerByNickname(key).alterFood(value.getFirst());
            this.getPlayerByNickname(key).alterPrestigePoints(value.getLast());
        });
        this.ui.showHuntEvent(effects);
    }

    public void applyPaintingEvent(Map<String, Integer> effects) {
        effects.forEach((key, value) -> this.getPlayerByNickname(key).alterFood(value));
        this.ui.showPaintingEvent(effects);
    }

    public void applySustenanceEventEffect(Map<String, List<Integer>> effects) {
        effects.forEach((key, value) -> {
            this.getPlayerByNickname(key).alterFood(value.getFirst());
            this.getPlayerByNickname(key).alterPrestigePoints(value.getLast());
        });
        this.ui.showSustenanceEvent(effects);
    }

    public void applyRitualEvent(Map<String, Integer> effects) {
        effects.forEach((key, value) -> this.getPlayerByNickname(key).alterPrestigePoints(value));
        this.ui.showRitualEvent(effects);
    }

    public void endRound(int currEra, List<Integer> topRow, List<Integer> bottomRow) {
        this.setTopRowCards(topRow);
        this.setBottomRowCards(bottomRow);
        this.currentEra = currEra;
    }

    public void endGame(List<String> winners) {
        this.winners.addAll(winners);
        this.ui.showGameEnd();
    }

    public List<String> getWinners() {
        return new ArrayList<>(this.winners);
    }

    public void applyModifier(String nickname, int modifier, boolean prestige) {
        if (prestige) {
            this.getPlayerByNickname(nickname).alterPrestigePoints(modifier);
        } else this.getPlayerByNickname(nickname).alterFood(modifier);
        this.ui.showOrderModifier(nickname, modifier, prestige);
    }

    public void resolveFoodOffer(String nickname) {
        this.getPlayerByNickname(nickname).alterFood(3);
        this.ui.showFoodOffer(nickname);
    }

    public void updateReconnectedLobby(List<String> reconnectedPlayers) {
        this.ownLobby.setCurrentPlayers(reconnectedPlayers.size());
        this.getAllPlayers().stream().filter(player -> !reconnectedPlayers.contains(player.getNickname())).forEach(player -> player.setDisconnected(true));
        this.ui.showReconnectionLobby();
    }

    public void restartGame(List<ClientPlayer> players, String currentPlayerNickname, List<Integer> topRowCards, List<Integer> bottomRowCards, int era, GamePhase phase, List<OfferTrackElement> offerTrack, List<Color> orderQueue) {
        this.ownPlayer = players.stream().filter(player -> player.getNickname().equals(this.ownPlayer.getNickname())).toList().getFirst();
        this.otherPlayers = players.stream().filter(player -> !player.getNickname().equals(this.ownPlayer.getNickname())).toList();
        this.currentEra = era;
        this.phase = phase;
        this.topRowCards.clear();
        this.topRowCards.addAll(topRowCards);
        this.bottomRowCards.clear();
        this.bottomRowCards.addAll(bottomRowCards);
        this.offerTrack.clear();
        this.offerTrack.addAll(offerTrack);
        this.orderQueue.clear();
        this.orderQueue.addAll(orderQueue);
        this.validating = false;
        this.currPlayerNickname = currentPlayerNickname;
        this.ui.showGameStart();
    }
}