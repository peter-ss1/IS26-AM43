package it.polimi.ingsw.am43.client;

import it.polimi.ingsw.am43.client.view.UI;
import it.polimi.ingsw.am43.database.RankElement;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.enums.PlayerStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The ClientModel class represents the local client-side state of the game.
 * It caches and tracks information regarding lobbies, players, cards, tracks,
 * and game phases, providing methods to update the state and notify the UI.
 */
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
    private boolean gameStarted;

    /**
     * Constructs a new ClientModel associated with the given user interface.
     *
     * @param ui The user interface instance to update.
     */
    public ClientModel(UI ui) {
        this.ui = ui;
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
        this.currPlayerNickname = "";
        this.validating = false;
        this.winners = new ArrayList<>();
        this.gameStarted = false;
    }

    /**
     * Resets the entire client model state to its initial default state.
     */
    public void reset() {
        this.lobbies.clear();
        this.ownLobby = new LobbyInfo(0, 0, 0);
        this.ownPlayer = null;
        this.otherPlayers.clear();
        this.topRowCards.clear();
        this.bottomRowCards.clear();
        this.orderQueue.clear();
        this.offerTrack.clear();
        this.phase = null;
        this.currentEra = 0;
        this.currPlayerNickname = "";
        this.validating = false;
        this.winners.clear();
        this.gameStarted = false;
    }

    /**
     * Refreshes the list of available server lobbies and notifies the UI
     * to transition to the lobby choice view.
     *
     * @param lobbies The updated list of available lobbies.
     */
    public void refreshLobbies(List<LobbyInfo> lobbies) {
        this.reset();
        this.lobbies.clear();
        this.lobbies.addAll(lobbies);
        this.ui.enterLobbyChoice();
    }

    /**
     * Gets the total number of players currently in the game.
     *
     * @return The number of remote players plus the local player.
     */
    public int getNumPlayers() {
        return otherPlayers.size() + 1;
    }

    /**
     * Returns a copy of the current offer track list.
     *
     * @return A new list containing the offer track elements.
     */
    public List<OfferTrackElement> getOfferTrack() {
        return new ArrayList<>(this.offerTrack);
    }

    /**
     * Determines which player colors are still available to be selected.
     *
     * @return A list of colors not chosen by any remote players.
     */
    public List<Color> getAvailableColors() {
        return Arrays.stream(Color.values())
                .filter(color -> this.otherPlayers.stream().noneMatch(otherPlayer -> otherPlayer.getColor().equals(color)))
                .toList();
    }

    /**
     * Gets the information of the lobby currently joined by the client.
     *
     * @return The client's own lobby info.
     */
    public LobbyInfo getOwnLobby() {
        return ownLobby;
    }

    /**
     * Sets the client's own lobby configuration and updates the UI view.
     *
     * @param ownLobby The target lobby information.
     */
    public void setOwnLobby(LobbyInfo ownLobby) {
        this.ownLobby = ownLobby;
        ui.enterLobby();
    }

    /**
     * Gets the client player object representing the local user.
     *
     * @return The own player object.
     */
    public ClientPlayer getOwnPlayer() {
        return ownPlayer;
    }

    /**
     * Sets the client player object for the local user.
     *
     * @param ownPlayer The client player object.
     */
    public void setOwnPlayer(ClientPlayer ownPlayer) {
        this.ownPlayer = ownPlayer;
    }

    /**
     * Sets the list of remote players, filtering out the local user if present.
     *
     * @param otherPlayers The list of all other session players.
     */
    public void setOtherPlayers(List<ClientPlayer> otherPlayers) {
        this.otherPlayers.clear();
        if (this.ownPlayer != null)
            otherPlayers = otherPlayers.stream().filter(p -> !p.getNickname().equals(this.ownPlayer.getNickname())).toList();
        this.otherPlayers.addAll(otherPlayers);
    }

    /**
     * Returns a copy of the numerical identifiers of the cards in the top row.
     *
     * @return A list of integer IDs representing top row cards.
     */
    public List<Integer> getTopRowCards() {
        return new ArrayList<>(topRowCards);
    }

    /**
     * Updates the local collection identifying the cards in the top row.
     *
     * @param topRowCards The new top row card IDs.
     */
    public void setTopRowCards(List<Integer> topRowCards) {
        this.topRowCards.clear();
        if (topRowCards != null) {
            this.topRowCards.addAll(topRowCards);
        }
    }

    /**
     * Returns a copy of the numerical identifiers of the cards in the bottom row.
     *
     * @return A list of integer IDs representing bottom row cards.
     */
    public List<Integer> getBottomRowCards() {
        return new ArrayList<>(bottomRowCards);
    }

    /**
     * Updates the local collection identifying the cards in the bottom row.
     *
     * @param bottomRowCards The new bottom row card IDs.
     */
    public void setBottomRowCards(List<Integer> bottomRowCards) {
        this.bottomRowCards.clear();
        if (bottomRowCards != null) {
            this.bottomRowCards.addAll(bottomRowCards);
        }
    }

    /**
     * Retrieves a copy of the sequence array representing the player turn order.
     *
     * @return The ordered list of colors.
     */
    public List<Color> getOrderQueue() {
        return new ArrayList<>(orderQueue);
    }

    /**
     * Gets the active era.
     *
     * @return The current era value.
     */
    public int getCurrentEra() {
        return currentEra;
    }

    /**
     * Returns a copy of the full collection tracking all known server lobbies.
     *
     * @return A list of available server lobbies.
     */
    public List<LobbyInfo> getLobbies() {
        return new ArrayList<>(lobbies);
    }

    /**
     * Finds and extracts a matching player profile utilizing the nickname.
     *
     * @param nickname The unique player name string.
     * @return The matching ClientPlayer component, or {@code null} if not found.
     */
    public ClientPlayer getPlayerByNickname(String nickname) {
        return this.getAllPlayers().stream()
                .filter(player -> player.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);
    }

    /**
     * Places a player's totem onto a specific offer track position.
     *
     * @param nickname The player performing the action.
     * @param position The targeted offer track index slot.
     */
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

    /**
     * Completes a player's active turn, modifying the offer track and order queue.
     *
     * @param nickname The involved player.
     */
    public void endTurn(String nickname) {
        Color color = getPlayerByNickname(nickname).getColor();
        this.offerTrack.stream().filter(o -> o.getColor() != null && o.getColor().equals(color)).findFirst().ifPresent(o -> {
            o.setColor(null);
        });
        if (!this.orderQueue.contains(color)) this.orderQueue.add(color);
        this.validating = false;
    }

    /**
     * Adds a lobby with its data to the local collection, replacing the previous version if already present.
     *
     * @param lobbyInfo The updated lobby record structure.
     */
    public void addLobby(LobbyInfo lobbyInfo) {
        this.lobbies.removeIf(l -> l.getLobbyId() == lobbyInfo.getLobbyId());
        if (lobbyInfo.getCurrentPlayers() != 0 && lobbyInfo.getNumPlayers() != lobbyInfo.getCurrentPlayers()) {
            this.lobbies.add(lobbyInfo);
        }
        this.ui.showAvailableLobbies();
    }

    /**
     * Gets the nickname of the currently active player.
     *
     * @return The current active player nickname string.
     */
    public String getCurrentPlayerNickname() {
        return this.currPlayerNickname;
    }

    /**
     * Assesses if a nickname string is already taken by another player.
     *
     * @param nickname The text input to evaluate.
     * @return {@code true} if available, {@code false} otherwise.
     */
    public boolean isNicknameAvailable(String nickname) {
        return this.otherPlayers.stream().noneMatch(player -> player.getNickname().equals(nickname));
    }

    /**
     * Assesses if a color selection is already taken by another player.
     *
     * @param color The color enumeration option to evaluate.
     * @return {@code true} if available, {@code false} otherwise.
     */
    public boolean isColorAvailable(Color color) {
        return this.otherPlayers.stream().noneMatch(player -> player.getColor().equals(color));
    }

    /**
     * Unions the client profile and remote players into a uniform list.
     *
     * @return A joint list tracking all players.
     */
    public List<ClientPlayer> getAllPlayers() {
        List<ClientPlayer> allPlayers = new ArrayList<>();
        if (this.ownPlayer != null) allPlayers.add(this.ownPlayer);
        allPlayers.addAll(this.otherPlayers);
        return allPlayers;
    }

    /**
     * Inserts new player data into local collections, starting from the own identity.
     *
     * @param nickname The identifier of the joining player.
     * @param color    The color selected by the joining player.
     */
    public void addPlayer(String nickname, Color color) {
        if (this.ownPlayer == null || !this.ownPlayer.getNickname().equals(nickname))
            this.otherPlayers.add(new ClientPlayer(nickname, color));
        this.ui.showNewPlayer();
    }

    /**
     * Gets the current game phase.
     *
     * @return The active GamePhase enum object.
     */
    public GamePhase getPhase() {
        return this.phase;
    }

    /**
     * Changes current game phase and updates internal state accordingly.
     *
     * @param phase The new GamePhase enum object.
     */
    public void setPhase(GamePhase phase) {
        this.phase = phase;
        if (this.phase == GamePhase.ROUND_ENDING) {
            this.ui.showRoundEnding();
            this.getAllPlayers().stream().filter(p -> p.getStatus().equals(PlayerStatus.WAITING)).forEach(p -> {
                p.setStatus(PlayerStatus.ACTIVE);
                this.orderQueue.add(p.getColor());
            });
        }
    }

    /**
     * Sets the updated current player.
     *
     * @param nickname The new current player.
     */
    public void setCurrentPlayer(String nickname) {
        this.currPlayerNickname = nickname;
        this.ui.showNewCurrPlayer();
    }

    /**
     * Initializes game specific data structures with initial setup data.
     *
     * @param initialFood           Map compiling player starting food.
     * @param currentPlayerNickname The nickname of the initial active player.
     * @param topRowCards           IDs of the initial top row cards.
     * @param bottomRowCards        IDs of the initial bottom row cards.
     * @param orderQueue            Initial turn order.
     * @param offerTrack            Offer track setup based on number of players.
     */
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
        this.gameStarted = true;
        this.ui.showGameStart();
    }

    /**
     * Maps displayed positions to underlying card identifiers.
     *
     * @param row The row label identifier (top or bottom).
     * @param pos The position coordinate.
     * @return The ID of the matching card.
     * @throws IllegalArgumentException If the position is not valid.
     */
    public int getIdByPos(String row, int pos) {
        if (row.equalsIgnoreCase("top")) {
            if (pos < 0 || pos >= this.topRowCards.size()) throw new IllegalArgumentException("Invalid row position");
            return topRowCards.get(pos);
        } else {
            if (pos < 0 || pos >= bottomRowCards.size()) throw new IllegalArgumentException("Invalid row position");
            return bottomRowCards.get(pos);
        }
    }

    /**
     * Evaluates validation blocking flag.
     *
     * @return {@code true} if command validation is occurring, {@code false} otherwise.
     */
    public boolean isValidating() {
        return this.validating;
    }

    /**
     * Assesses if active player matches own client.
     *
     * @return {@code true} if it matches the client user, {@code false} otherwise.
     */
    public boolean isOwnTurn() {
        return this.ownPlayer.getNickname().equals(this.currPlayerNickname);
    }

    /**
     * Evaluates if specified nickname matches existing player.
     *
     * @param nickname The string to evaluate.
     * @return {@code true} if matched, {@code false} otherwise.
     */
    public boolean isPlayer(String nickname) {
        return this.getAllPlayers().stream().anyMatch(player -> player.getNickname().equals(nickname));
    }

    /**
     * Activates verification locks blockading local interactive elements.
     */
    public void startValidation() {
        this.validating = true;
    }

    /**
     * Clears verification blocks releasing interactive elements.
     */
    public void stopValidation() {
        this.validating = false;
    }

    /**
     * Extracts picked card ID from board to player tribe.
     *
     * @param nickname  The player performing the action.
     * @param cardId    The picked card ID.
     * @param finalPick Validation flag denoting ending turn.
     */
    public void pickCard(String nickname, int cardId, boolean finalPick) {
        this.getPlayerByNickname(nickname).updateTribe(cardId);
        this.topRowCards.remove((Integer) cardId);
        this.bottomRowCards.remove((Integer) cardId);
        this.ui.showCardPicked(nickname, cardId, finalPick);
        this.validating = false;
    }

    /**
     * Updates player resources after building acquisition.
     *
     * @param nickname The affected player.
     * @param cost     The deducted food amount.
     */
    public void buyBuilding(String nickname, int cost) {
        this.getPlayerByNickname(nickname).alterFood(-cost);
        this.ui.showBuildingAcquisition(nickname, cost);
    }

    /**
     * Updates player resources after active hunter acquisition.
     *
     * @param nickname The affected player.
     * @param food     The gained food amount.
     */
    public void hunterEffect(String nickname, int food) {
        this.getPlayerByNickname(nickname).alterFood(food);
        this.ui.showHunterEffect(nickname, food);
    }

    /**
     * Resolves bonus alterations triggered by a building card.
     *
     * @param nickname The affected player.
     * @param bonus    The numerical amount of resource involved.
     * @param resource The type of resource involved.
     */
    public void applyBuildingEffect(String nickname, int bonus, String resource) {
        if (resource.equalsIgnoreCase("food")) {
            this.getPlayerByNickname(nickname).alterFood(bonus);
        }
        if (resource.equalsIgnoreCase("prestige points")) {
            this.getPlayerByNickname(nickname).alterPrestigePoints(bonus);
        }
        this.ui.showBuildingEffect(nickname, bonus, resource);
    }

    /**
     * Updates players' data with the effects of a hunt event resolution.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     *                in food and prestige points.
     */
    public void applyHuntEventEffect(Map<String, PointsPair> effects) {
        effects.forEach((key, value) -> {
            this.getPlayerByNickname(key).alterFood(value.getFood());
            this.getPlayerByNickname(key).alterPrestigePoints(value.getPrestige());
        });
        this.ui.showHuntEvent(effects);
    }

    /**
     * Updates players' data with the effects of a painting event resolution.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     *                in prestige points.
     */
    public void applyPaintingEvent(Map<String, Integer> effects) {
        effects.forEach((key, value) -> this.getPlayerByNickname(key).alterPrestigePoints(value));
        this.ui.showPaintingEvent(effects);
    }

    /**
     * Updates players' data with the effects of a sustenance event resolution.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     *                in food and prestige points.
     */
    public void applySustenanceEventEffect(Map<String, PointsPair> effects) {
        effects.forEach((key, value) -> {
            this.getPlayerByNickname(key).alterFood(value.getFood());
            this.getPlayerByNickname(key).alterPrestigePoints(value.getPrestige());
        });
        this.ui.showSustenanceEvent(effects);
    }

    /**
     * Updates players' data with the effects of a shaman event resolution.
     *
     * @param effects A map pairing player nicknames with their respective adjustments
     *                in prestige points.
     */
    public void applyRitualEvent(Map<String, Integer> effects) {
        effects.forEach((key, value) -> this.getPlayerByNickname(key).alterPrestigePoints(value));
        this.ui.showRitualEvent(effects);
    }

    /**
     * Swaps row states according to game rules.
     *
     * @param currEra   The updated era value.
     * @param topRow    The IDs of cards of the updated top row.
     * @param bottomRow The IDs of cards of the updated bottom row.
     */
    public void endRound(int currEra, List<Integer> topRow, List<Integer> bottomRow) {
        this.setTopRowCards(topRow);
        this.setBottomRowCards(bottomRow);
        this.currentEra = currEra;
    }

    /**
     * Finalizes victory arrays mapping total points.
     *
     * @param winners     The collection listing winning players.
     * @param finalPoints Total scores compiled per player.
     */
    public void endGame(List<String> winners, Map<String, Integer> finalPoints) {
        this.winners.clear();
        this.winners.addAll(winners);
        this.getAllPlayers().forEach(player -> {
            if (finalPoints.containsKey(player.getNickname()))
                player.setPrestigePoints(finalPoints.get(player.getNickname()));
        });
        this.ui.showFinalPoints();
    }

    /**
     * Gets collection containing winning players.
     *
     * @return The winning players collection list.
     */
    public List<String> getWinners() {
        return new ArrayList<>(this.winners);
    }

    /**
     * Updates player resources with modifiers triggered by turn order.
     *
     * @param nickname The affected player.
     * @param modifier The involved bonus/malus.
     * @param prestige {@code true} if the change alters prestige points,
     *                 {@code false} if it applies to food.
     */
    public void applyModifier(String nickname, int modifier, boolean prestige) {
        if (prestige) {
            this.getPlayerByNickname(nickname).alterPrestigePoints(modifier);
        } else this.getPlayerByNickname(nickname).alterFood(modifier);
        this.ui.showOrderModifier(nickname, modifier, prestige);
    }

    /**
     * Updates player food based on offer track card bonus.
     *
     * @param nickname The affected player.
     */
    public void resolveFoodOffer(String nickname) {
        this.getPlayerByNickname(nickname).alterFood(3);
        this.ui.showFoodOffer(nickname);
    }

    /**
     * Updates to the connection and game status of a player.
     *
     * @param reconnectedPlayer The affected player.
     */
    public void reconnectPlayer(String reconnectedPlayer) {
        if (reconnectedPlayer.equalsIgnoreCase(this.ownPlayer.getNickname())) return;
        this.getPlayerByNickname(reconnectedPlayer).setStatus(PlayerStatus.WAITING);
        this.ui.showPlayerReconnection(reconnectedPlayer);
    }

    /**
     * Restores local state from retrieved backup.
     *
     * @param players               The list containing all players.
     * @param currentPlayerNickname The nickname of the current active player.
     * @param topRowCards           IDs of the retrieved top row cards.
     * @param bottomRowCards        IDs of the retrieved bottom row cards.
     * @param era                   Retrieved current era value.
     * @param phase                 Retrieved active game phase.
     * @param offerTrack            Retrieved totem positioning.
     * @param orderQueue            Retrieved turn order.
     */
    public void restartGame(List<ClientPlayer> players, String currentPlayerNickname, List<Integer> topRowCards, List<Integer> bottomRowCards, int era, GamePhase phase, List<OfferTrackElement> offerTrack, List<Color> orderQueue) {
        this.ownPlayer = players.stream().filter(player -> player.getNickname().equals(this.ownPlayer.getNickname())).toList().getFirst();
        this.otherPlayers = new ArrayList<>(players.stream().filter(player -> !player.getNickname().equals(this.ownPlayer.getNickname())).toList());
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
        this.gameStarted = true;
        this.currPlayerNickname = currentPlayerNickname;
        this.ui.showGameStart();
    }

    /**
     * Recovers previous player configuration data.
     *
     * @param nickname The previous unique identifier.
     * @param color    The previous color selection.
     */
    public void retrieveOldPlayer(String nickname, Color color) {
        this.ownPlayer = new ClientPlayer(nickname, color);
        this.ui.showRetrievedInfo();
    }

    /**
     * Executes internal changes processing player disconnection.
     *
     * @param nickname The disconnected player.
     */
    public void disconnectPlayer(String nickname) {
        this.otherPlayers.stream().filter(p -> p.getNickname().equals(nickname))
                .findFirst().ifPresentOrElse(p -> {
                    if (this.gameStarted) {
                        p.setStatus(PlayerStatus.INACTIVE);
                        this.offerTrack.stream().filter(o -> o.getColor() != null && o.getColor().equals(p.getColor())).findFirst().ifPresent(o -> {
                            o.setColor(null);
                        });
                        this.orderQueue.remove(p.getColor());
                    } else {
                        this.ownLobby.setCurrentPlayers(this.ownLobby.getCurrentPlayers() - 1);
                        this.otherPlayers.remove(p);
                    }
                }, () -> this.ownLobby.setCurrentPlayers(this.ownLobby.getCurrentPlayers() - 1));
        this.ui.showDisconnectedPlayer(nickname);
    }

    /**
     * Gathers players based on INACTIVE game status.
     *
     * @return Colors belonging to disconnected participants.
     */
    public List<Color> getInactivePlayers() {
        return new ArrayList<>(this.getAllPlayers().stream().filter(p -> p.getStatus().equals(PlayerStatus.INACTIVE)).map(p -> p.getColor()).toList());
    }

    /**
     * Gathers players based on WAITING game status.
     *
     * @return Colors belonging to waiting participants.
     */
    public List<Color> getWaitingPlayers() {
        return new ArrayList<>(this.getAllPlayers().stream().filter(p -> p.getStatus().equals(PlayerStatus.WAITING)).map(p -> p.getColor()).toList());
    }

    /**
     * Updates internal state with winner data and notifies UI of game over with ranking data.
     *
     * @param leaderboard Sorted array lists tracking the final rankings.
     * @param playerRanks Map detailing individual position indexes.
     */
    public void showLeaderboard(List<RankElement> leaderboard, Map<String, Integer> playerRanks) {
        if (this.winners.isEmpty()) this.winners.add(this.ownPlayer.getNickname());
        this.ui.showGameEnd(leaderboard, playerRanks.get(this.ownPlayer.getNickname()));
    }

    /**
     * Notifies UI of game flow suspension with timer.
     */
    public void startSinglePlayerTimer() {
        this.ui.showTimer(60);
    }
}