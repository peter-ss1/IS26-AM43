package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents the game board containing the order queue, decks, offer track,
 * rows of cards, and manages round updates and active players.
 */
public class Board implements Serializable {

    private final OrderQueue turnOrder;
    private final TribeDeck tribeDeck;
    private final BuildingDeck buildingDeck;
    private final Row topRow;
    private final Row bottomRow;
    private final List<OfferTrackCard> offerTrack;
    private int currEra;
    private int playersOnBoard;

    /**
     * Constructs a new Board, initializes rows, decks, and queues, and seeds
     * the initial setup of cards across the rows.
     *
     * @param players        the list of participating players
     * @param numPlayers     the total number of players
     * @param foodModifiers  the list of food modifiers for the turn order queue
     * @param tribeDeck      the list of cards to populate the tribe deck
     * @param buildingDeck   the map of buildings categorized by era
     * @param offerTrack     the tiles of the offer track
     * @param seed           the random seed used to shuffle turn order
     * @throws RuntimeException if an unexpected action type is encountered while drawing cards
     */
    public Board(List<Player> players, int numPlayers, List<Integer> foodModifiers, List<Card> tribeDeck, Map<Integer, List<Building>> buildingDeck, List<OfferTrackCard> offerTrack, long seed) throws RuntimeException {
        this.turnOrder = new OrderQueue(players, foodModifiers, seed);
        this.tribeDeck = new TribeDeck(tribeDeck);
        this.buildingDeck = new BuildingDeck(buildingDeck);
        this.topRow = new Row();
        this.bottomRow = new Row();
        this.offerTrack = offerTrack;
        this.currEra = 1;
        this.playersOnBoard=numPlayers;
        while (this.bottomRow.size() <= numPlayers) {
            Card cardDrawn = this.tribeDeck.draw();
            switch (cardDrawn.firstRowChoice()) {
                case OfferAction.TOP:
                    cardDrawn.addToRow(this.topRow);
                    break;
                case OfferAction.BOTTOM:
                    cardDrawn.addToRow(this.bottomRow);
                    break;
                default:
                    throw new RuntimeException("error in draw");
            }
        }
        while (this.topRow.size() < numPlayers + 4) {
            this.tribeDeck.draw().addToRow(this.topRow);
        }
        for (Building building : this.buildingDeck.revealEra(1)) {
            building.addToRow(this.topRow);
        }
    }

    /**
     * Removes the player from the board (either from the order queue or the offer track)
     * and decrements the active-players counter.
     *
     * @param player the player to remove
     */
    public void removePlayerFromBoard(Player player){
        if(!this.turnOrder.removePlayer(player)){
            for (OfferTrackCard otc : this.offerTrack){
                if(otc.getPlayer().isPresent() && otc.getPlayer().get().equals(player))
                    otc.removePlayer();
            }
        }
        this.playersOnBoard--;
        return;

    }

    /** Increments the active-players counter on the board (used when a WAITING player wakes up). */
    public void addPlayerFromBoard(){
        this.playersOnBoard++;
    }

    /** @return the current era (1, 2 or 3) */
    public int getCurrEra() {
        return this.currEra;
    }

    /**
     * Places the player on the given offer track slot.
     *
     * @param player   the player to place
     * @param position the chosen position (0-based)
     * @throws IndexOutOfBoundsException if the position is not valid
     * @throws IllegalMoveException      if the slot is already occupied
     */
    public void setPlayerOnTrack(Player player, int position) throws IndexOutOfBoundsException, IllegalMoveException {
        if (position < 0 || position > this.offerTrack.size()) throw new IndexOutOfBoundsException("Invalid position");
        if (this.offerTrack.get(position).getPlayer().isPresent())
            throw new IllegalMoveException("Cannot pick occupied tile");
        this.offerTrack.get(position).setPlayer(player);
    }

    /**
     * Advances to the next era: removes the buildings from the bottom, moves those in the top
     * to the bottom and reveals the new buildings of the next era in the top.
     *
     * @throws IllegalStateException if it tries to go past era 3
     */
    public void increaseCurrEra() {
        if (++this.currEra > 3) throw new IllegalStateException("Era does not exist");
        this.bottomRow.removeBuildings();
        this.moveTopToBottomBuildings();
        this.topRow.addAllBuildings(this.buildingDeck.revealEra(this.currEra));
    }

    /**
     * Draws {@code amount} cards from the tribe deck and adds them to the top row.
     * If a card belongs to an era later than the current one, it increments the era.
     *
     * @param amount the number of cards to draw
     */
    public void replenishTopRow(int amount) {
        for (int i = 0; i < amount; i++) {
            Card card = this.tribeDeck.draw();
            card.addToRow(this.topRow);
            if (card.getEra() > this.currEra) {
                this.increaseCurrEra();
            }
        }
    }

    /**
     * @param card the card whose position to look up
     * @return TOP if the card is in the top row, BOTTOM if in the bottom row
     * @throws IllegalArgumentException if the card is not on the board
     */
    public OfferAction getCardPosition(Card card) {
        if (card.isContainedInRow(this.topRow)) return OfferAction.TOP;
        if (card.isContainedInRow(this.bottomRow)) return OfferAction.BOTTOM;
        throw new IllegalArgumentException("card not in board");
    }

    /**
     * Removes the card from the row it is in (top or bottom).
     *
     * @param card the card to remove
     */
    public void removeCard(Card card) {
        if (card.isContainedInRow(this.topRow)) {
            card.removeFromRow(this.topRow);
            return;
        }
        if (card.isContainedInRow(this.bottomRow)) {
            card.removeFromRow(this.bottomRow);
        }
    }

    /** @return the next player in the order queue without removing them */
    public Player getNextPlayerInOrderQueue() {
        return this.turnOrder.peek();
    }

    /** Removes the next player from the order queue (called after they have chosen their position on the offer track). */
    public void popNextPlayerInOrderQueue() {
        this.turnOrder.pop();
    }

    /**
     * @return the next player on the offer track (the first occupied slot), or empty if no one is placed
     */
    public Optional<Player> getNextPlayerOnOfferTrack() {
        for (OfferTrackCard otd : this.offerTrack) {
            if (otd.getPlayer().isPresent()) return otd.getPlayer();
        }
        return Optional.empty();
    }

    /**
     * @param card the card to look for
     * @return true if the card is present in one of the two board rows
     */
    public boolean containsCard(Card card) {
        return card.isContainedInRow(this.topRow) || card.isContainedInRow(this.bottomRow);
    }

    /**
     * Activates all the events in the given row, notifying the observer for each effect.
     *
     * @param observer the observer to notify
     * @param row      the row (TOP or BOTTOM) in which to activate the events
     * @param players  the list of all players
     */
    public void activateEvents(GameObserver observer, OfferAction row, List<Player> players) {
        switch (row) {
            case OfferAction.TOP:
                this.topRow.activateEvents(observer, players);
                return;
            case OfferAction.BOTTOM:
                this.bottomRow.activateEvents(observer, players);
                return;
            default:
                throw new RuntimeException("error in activate events");
        }
    }

    /**
     * Moves characters and events from the top row to the bottom row (start of a new round).
     * It first removes the characters/events already present in the bottom row.
     */
    public void moveTopToBottomTribe() {
        this.bottomRow.removeCharacters();
        this.bottomRow.removeEvents();
        this.bottomRow.addAllCharacters(this.topRow.getAllCharacters());
        this.bottomRow.addAllEvents(this.topRow.getAllEvents());
        this.topRow.removeCharacters();
        this.topRow.removeEvents();
    }

    /** Moves the buildings from the top row to the bottom row (era change). */
    public void moveTopToBottomBuildings() {
        this.bottomRow.addAllBuildings(this.topRow.getAllBuildings());
        this.topRow.removeBuildings();
    }

    /**
     * Removes the player from their offer track slot and reinserts them into the order queue.
     *
     * @param observer the observer to notify on reinsertion
     * @param player   the player to requeue
     */
    public void returnPlayerToOrderQueue(GameObserver observer, Player player) {
        for (OfferTrackCard otd : this.offerTrack) {
            if (otd.getPlayer().isPresent() && otd.getPlayer().get().equals(player)) {
                otd.removePlayer();
                break;
            }
        }
        this.turnOrder.append(observer, player);
    }

    /** @return true if the order queue is empty (all players are on the offer track) */
    public boolean isOrderQueueEmpty() {
        return this.turnOrder.isEmpty();
    }

    /**
     * Checks whether the player can end the turn: every row in which they have
     * available actions must be free of pickable characters.
     *
     * @param player the current player
     * @return true if the turn can be ended
     */
    public boolean checkEndTurnCondition(Player player) {
        for (OfferAction action : player.getAvailableActions()) {
            switch (action) {
                case TOP:
                    if (!topRow.getAllCharacters().isEmpty()) {
                        return false;
                    }
                    break;
                case BOTTOM:
                    if (!bottomRow.getAllCharacters().isEmpty()) {
                        return false;
                    }
                    break;
                default:
            }
        }
        return true;
    }

    /**
     * Checks whether all the player's available actions have been resolved.
     * If the FOOD action is left, it directly adds 3 food and considers it resolved.
     *
     * @param observer the observer used to notify the possible food bonus
     * @param player   the current player
     * @return true if there are no more pending actions
     */
    public boolean isOfferResolved(GameObserver observer, Player player) {
        for (OfferAction action : player.getAvailableActions()) {
            switch (action) {
                case TOP, BOTTOM:
                    if (this.pickableCards(action, player)) return false;
                    break;
                case FOOD:
                    player.alterFood(3);
                    observer.broadcast(new Update.FoodOfferUpdate(player.getNickname()));
                    return true;
            }
        }
        return true;
    }

    /** @return true if the tribe deck is exhausted (the last round is being played) */
    public boolean checkFinalRound() { return tribeDeck.isEmpty();
    }

    /** @return true if all active players are in the order queue */
    public boolean isOrderQueueFull() {
        return turnOrder.size()==this.playersOnBoard;
    }

    /**
     * Checks whether the given row contains cards the player can actually pick
     * (characters present, or buildings with cost ≤ available food after discount).
     *
     * @param row    the row to check (TOP or BOTTOM)
     * @param player the current player
     * @return true if at least one pickable card exists
     */
    public boolean pickableCards(OfferAction row, Player player) {
        switch (row) {
            case TOP:
                if (topRow.getAllCharacters().isEmpty() && (topRow.getAllBuildings().isEmpty() || topRow.getAllBuildings().stream().noneMatch(building -> building.getCost() <= (player.getFood() - player.getBuildingDiscount())))) {
                    return false;
                }
                break;
            case BOTTOM:
                if (bottomRow.getAllCharacters().isEmpty() && (bottomRow.getAllBuildings().isEmpty() || bottomRow.getAllBuildings().stream().noneMatch(building -> building.getCost() <= (player.getFood() - player.getBuildingDiscount())))) {
                    return false;
                }
                break;
        }
        return true;
    }

    /** @return true if the last food bonus granted by the order queue is positive */
    public boolean hasFoodBonus() {
        return turnOrder.getLastFoodGiven() > 0;
    }

    /**
     * Builds and broadcasts the game-start update with the full initial state
     * (food, first player, board rows, order queue, offer track).
     *
     * @param observer the observer to notify
     * @param players  the list of all players
     * @param nickname the nickname of the first current player
     */
    public void buildGameStartedUpdate(GameObserver observer, List<Player> players, String nickname) {
        observer.broadcast(new Update.GameStartedUpdate(
                players.stream().collect(Collectors.toMap(Player::getNickname, Player::getFood)),
                nickname,
                this.topRow.getIds(),
                this.bottomRow.getIds(),
                this.turnOrder.getColorOrder(),
                this.offerTrack.stream().map(card -> new OfferTrackElement(card.getActions(), null)).toList()
        ));
    }

    /**
     * Builds and broadcasts the new-round update with the current era and the visible cards.
     *
     * @param observer the observer to notify
     */
    public void buildNewRoundUpdate(GameObserver observer) {
        observer.broadcast(new Update.NewRoundUpdate(this.currEra, this.topRow.getIds(), this.bottomRow.getIds()));
    }

    /**
     * Builds the full snapshot of the current game (used when a player reconnects).
     *
     * @param players  the list of all players with their state
     * @param nickname the nickname of the current player
     * @param phase    the current game phase
     * @return a GameRestartedUpdate ready to be sent to the client
     */
    public Update.GameRestartedUpdate buildGameSnapshot(List<Player> players, String nickname, GamePhase phase) {
        return new Update.GameRestartedUpdate(
                players.stream().map(p -> new ClientPlayer(
                                p.getNickname(),
                                p.getColor(),
                                p.getFood(),
                                p.getPrestigePoints(),
                                p.getTribe().getIds(),
                                p.getStatus())).toList()
                        ,
                        nickname,
                        this.topRow.getIds(),
                        this.bottomRow.getIds(),
                        this.turnOrder.getColorOrder(),
                        this.offerTrack.stream().map(card -> new OfferTrackElement(card.getActions(), card.getPlayer().map(Player::getColor).orElse(null))).toList(),
                        this.currEra,
                        phase
                );
    }
}
