package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.enums.PlayerStatus;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.exceptions.IllegalPlayerInitializationException;
import it.polimi.ingsw.am43.model.exceptions.OutOfTurnException;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Game implements ModelInterface, Serializable {
    private final List<Color> availableColors;
    private final List<Player> players;
    private final int numPlayers;
    private Player currPlayer;
    private GamePhase phase;
    private Board board;
    private final Map<Integer, Card> idToCard;
    private transient GameObserver observer;
    private long seed;

    public Game(int np, String nk, Color color) {
        this.availableColors = new ArrayList<>(Arrays.asList(Color.values()));
        this.numPlayers = np;
        this.phase = GamePhase.PREPARATION;
        this.players = new ArrayList<>();
        this.idToCard = new HashMap<>();
        this.seed = ThreadLocalRandom.current().nextLong();
        this.addPlayer(nk, color);
    }

    public Game(int np, String nk, Color color, Long seed) {
        this.availableColors = new ArrayList<>(Arrays.asList(Color.values()));
        this.numPlayers = np;
        this.phase = GamePhase.PREPARATION;
        this.players = new ArrayList<>();
        this.idToCard = new HashMap<>();
        this.seed = seed;
        this.addPlayer(nk, color);
    }

    /**
     * Puts the player in the INACTIVE state and, if it was their turn, advances the phase.
     * If only one active player remains, it notifies single-player mode.
     *
     * @param player the player to make inactive
     */
    public void moveToInactive(Player player) throws IllegalArgumentException{
        player.setStatus(PlayerStatus.INACTIVE);
        List <Player>InGamePlayers=this.players.stream().filter(p-> !p.getStatus().equals(PlayerStatus.INACTIVE)).toList();
        if (InGamePlayers.isEmpty()){
            this.observer.endGame();
            return;
        }

        this.board.removePlayerFromBoard(player);
        if (this.currPlayer.equals(player)){
            if (this.getPhase().equals(GamePhase.OFFER_TRACK_SELECTION)){
                if (this.board.isOrderQueueEmpty()) {
                    this.getPhase().resolvePhase(this, this.board);
                }else {
                    this.setCurrPlayer(this.board.getNextPlayerInOrderQueue());
                }
            }else if(this.getPhase().equals(GamePhase.ACTION_RESOLUTION)){
                board.getNextPlayerOnOfferTrack().ifPresentOrElse(this::setCurrPlayer,
                        () -> {
                            this.wakeUpWaiting();
                            this.phase.resolvePhase(this, this.board);
                        });
            }

        }
        if (InGamePlayers.size()==1)
            this.observer.notifySinglePlayerGame(InGamePlayers.getFirst().getNickname());
    }

    /**
     * Brings an INACTIVE player back to the WAITING state and sends them the current game snapshot.
     *
     * @param player the player to re-admit as a spectator
     */
    public void moveToWait(Player player) throws IllegalArgumentException{
        if (!player.getStatus().equals(PlayerStatus.INACTIVE)) throw new IllegalArgumentException("player not inactive");
        player.setStatus(PlayerStatus.WAITING);
        this.observer.updatePlayer(player.getNickname(),this.board.buildGameSnapshot(this.getAllPlayers(), this.currPlayer.getNickname(), this.phase));
    }

    /**
     * Wakes up all WAITING players, bringing them back to ACTIVE
     * and reinserting them into the order queue.
     */
    public void wakeUpWaiting() {
        this.players.stream().filter(p->p.getStatus().equals(PlayerStatus.WAITING)).forEach(p->{
            p.setStatus(PlayerStatus.ACTIVE);
            this.board.addPlayerFromBoard();
            this.board.returnPlayerToOrderQueue(this.observer,p);
        });
    }

    /**
     * Sets the GameObserver. Must be called after loading from disk
     * since the field is transient and is lost during serialization.
     *
     * @param observer the controller that will receive the model's notifications
     */
    public void setObserver(GameObserver observer) {
        this.observer = observer;
    }

    /**
     * @return the list of colors still available for the player to choose
     */
    public ArrayList<Color> getAvailableColors() {
        return new ArrayList<>(this.availableColors);
    }

    /** @return the seed used for the game's random generation */
    public long getSeed() {
        return this.seed;
    }

    /**
     * Initializes the Board with the decks and the offer track and sets the first current player.
     *
     * @param players       the list of players
     * @param numPlayers    the total number of players
     * @param foodModifiers the food modifiers for the order queue
     * @param tribeDeck     the deck of tribe cards
     * @param buildingDeck  the building deck split by era
     * @param offerTrack    the offer track slots
     */
    public void initBoard(List<Player> players, int numPlayers, List<Integer> foodModifiers, List<Card> tribeDeck, Map<Integer, List<Building>> buildingDeck, List<OfferTrackCard> offerTrack) throws RuntimeException {
        this.board = new Board(players, numPlayers, foodModifiers, tribeDeck, buildingDeck, offerTrack, this.seed);
        this.currPlayer = this.board.getNextPlayerInOrderQueue();
    }

    /**
     * Loads the id→card map used by {@link #getCardById(int)}.
     *
     * @param map the map from unique id to the corresponding card
     */
    public void setIdCard(Map<Integer, Card> map) {
        this.idToCard.putAll(map);
    }

    /**
     * @return all players registered in the game (defensive copy)
     */
    public ArrayList<Player> getAllPlayers(){
        return new ArrayList<>(this.players);
    }

    /** @return players in the ACTIVE state */
    public ArrayList<Player> getActivePlayers() {
        return new ArrayList<>(this.players.stream().filter(p->p.getStatus().equals(PlayerStatus.ACTIVE)).toList());
    }

    /** @return players in the INACTIVE state */
    public ArrayList<Player> getInactivePlayers() {
        return new ArrayList<>(this.players.stream().filter(p->p.getStatus().equals(PlayerStatus.INACTIVE)).toList());
    }

    /** @return players in the WAITING state */
    public ArrayList<Player> getWaitingPlayers() {
        return new ArrayList<>(this.players.stream().filter(p->p.getStatus().equals(PlayerStatus.WAITING)).toList());
    }

    /**
     * Starts the game by resolving the initial phase and sending the GameStarted update to all clients.
     */
    public void startGame() {
        this.getPhase().resolvePhase(this, this.board);
        this.board.buildGameStartedUpdate(this.observer, this.players, this.currPlayer.getNickname());
    }

    /**
     * Adds a new player to the game during the PREPARATION phase.
     *
     * @param nickname the nickname chosen by the player
     * @param color    the color chosen by the player
     * @throws IllegalArgumentException              if the game is already full
     * @throws IllegalStateException                 if the phase is not PREPARATION
     */
    public void addPlayer(String nickname, Color color) {
        if (this.numPlayers == this.getAllPlayers().size()) throw new IllegalArgumentException("Game is already full");
        if (!phase.equals(GamePhase.PREPARATION))
            throw new IllegalStateException("Cannot add player when phase is " + phase);
        if (this.players.stream().anyMatch(p -> p.getNickname().equals(nickname)))
            throw new IllegalPlayerInitializationException("Nickname is already in use");
        if (!this.availableColors.remove(color))
            throw new IllegalPlayerInitializationException("Color is already in use");
        this.players.add(new Player(nickname, color));
    }

    /** @return the maximum number of players in the game */
    public int getNumPlayers() {
        return this.numPlayers;
    }

    /** @return the current player */
    public Player getCurrPlayer() {
        return this.currPlayer;
    }

    /**
     * Sets the current player and notifies all clients via broadcast.
     *
     * @param player the player who becomes current
     */
    public void setCurrPlayer(Player player) {
        if (!this.players.contains(player))
            throw new IllegalArgumentException(("Player is not registered in the game"));
        this.currPlayer = player;
        this.observer.broadcast(new Update.CurrentPlayerUpdate(this.currPlayer.getNickname()));
    }

    /** @return the current game phase */
    public GamePhase getPhase() {
        return this.phase;
    }

    /**
     * Sets the game phase and notifies all clients of the phase change.
     *
     * @param phase the new game phase
     */
    public void setPhase(GamePhase phase) {
        this.phase = phase;
        this.observer.broadcast(new Update.NewPhaseUpdate(this.phase));
    }

    /**
     * Places the player's totem on the offer track at the chosen position.
     * Checks that it is the player's turn and that the phase is OFFER_TRACK_SELECTION.
     *
     * @param player   the player placing the totem
     * @param position the position on the offer track (0-based)
     */
    public void placeTotemOnTrack(Player player, int position) {
        if (!this.players.contains(player))
            throw new IllegalArgumentException(("Player is not registered/active in the game"));
        if (!phase.equals(GamePhase.OFFER_TRACK_SELECTION))
            throw new IllegalStateException("Cannot place totem when phase is " + phase);
        if (!player.equals(this.currPlayer)) throw new OutOfTurnException("Cannot place totem in other player's turn");
        this.board.setPlayerOnTrack(player, position);
        this.board.popNextPlayerInOrderQueue();
        this.observer.broadcast(new Update.TotemPlacedUpdate(player.getNickname(), position));
        if (this.board.isOrderQueueEmpty()) {
            this.getPhase().resolvePhase(this, this.board);
            return;
        }
        this.setCurrPlayer(this.board.getNextPlayerInOrderQueue());
    }

    /**
     * Looks up and returns the player with the given nickname.
     *
     * @param name the player's nickname
     * @return the matching player
     * @throws IllegalArgumentException if no player has that nickname
     */
    public Player getPlayerByName(String name) {
        for (Player p : this.players) if (p.getNickname().equals(name)) return p;
        throw new IllegalArgumentException("Player is not registered in the game");

    }

    /**
     * Returns the card with the given id from the central registry.
     *
     * @param id the unique id of the card
     * @return the matching card
     * @throws IllegalArgumentException if the id is not registered
     */
    public Card getCardById(int id) throws IllegalArgumentException {
        if (!this.idToCard.containsKey(id)) throw new IllegalArgumentException("Card is not registered in the game");
        return this.idToCard.get(id);
    }

    /**
     * The current player picks a card from the board.
     * Checks turn, phase, available actions and the card's position.
     * Delegates the pickup effect to the card itself via {@link Card#pick}.
     *
     * @param card   the card to pick
     * @param player the player picking it
     */
    public void pickCard(Card card, Player player) {
        if (!this.board.containsCard(card)) throw new IllegalArgumentException("Card is not on the board");
        if (!this.players.contains(player))
            throw new IllegalArgumentException(("Player is not registered in the game"));
        if (!this.phase.equals(GamePhase.ACTION_RESOLUTION) && !this.phase.equals(GamePhase.DRAW_FROM_TOP_BONUS_ACTION))
            throw new IllegalStateException("Cannot pick card when phase is " + phase);
        if (!this.currPlayer.equals(player)) throw new OutOfTurnException("Cannot pick card in other player's turn");
        if (player.getAvailableActions().isEmpty()) throw new IllegalMoveException("Cannot pick another card");
        if (!player.getAvailableActions().contains(this.board.getCardPosition(card)))
            throw new IllegalMoveException("Cannot pick card in wrong row");
        card.pick(this.observer, player);
        player.removeAvailableAction(this.board.getCardPosition(card));
        this.board.removeCard(card);
        resolveOffer(player);
    }

    /**
     * Ends the current player's turn. Checks that there are no mandatory
     * remaining actions and advances to the next phase if needed.
     *
     * @param player the player who wants to end the turn
     */
    public void endCurrentTurn(Player player) {
        if (!this.players.contains(player))
            throw new IllegalArgumentException(("Player is not registered in the game"));
        if (!this.phase.equals(GamePhase.ACTION_RESOLUTION) && !this.phase.equals(GamePhase.DRAW_FROM_TOP_BONUS_ACTION))
            throw new IllegalStateException("Cannot end turn when phase is " + phase);
        if (!this.currPlayer.equals(player)) throw new OutOfTurnException("Cannot end turn in other player's turn");
        if (this.phase.equals(GamePhase.DRAW_FROM_TOP_BONUS_ACTION)) {
            this.observer.broadcast(new Update.TurnEndedUpdate(player.getNickname()));
            this.wakeUpWaiting();
            this.phase.resolvePhase(this, this.board);
            return;
        }
        if (board.checkEndTurnCondition(player)) {
            board.returnPlayerToOrderQueue(this.observer, player);
            this.observer.broadcast(new Update.TurnEndedUpdate(player.getNickname()));
            player.getTribe().activateTimedBuilding(this, player, this.board);
            board.getNextPlayerOnOfferTrack().ifPresentOrElse(this::setCurrPlayer,
                    () -> {
                            this.wakeUpWaiting();
                            this.phase.resolvePhase(this, this.board);
                    });
        } else throw new IllegalMoveException("Available actions remaining");
    }

    /**
     * Resolves the player's offer: if their actions are exhausted or the row is empty,
     * it advances the turn to the next player on the offer track.
     *
     * @param player the current player
     */
    public void resolveOffer(Player player) {
        if (player.getAvailableActions().isEmpty() || board.isOfferResolved(this.observer, player)) {
            if (!this.getPhase().equals(GamePhase.DRAW_FROM_TOP_BONUS_ACTION))
                board.returnPlayerToOrderQueue(this.observer, player);
            this.observer.broadcast(new Update.TurnEndedUpdate(player.getNickname()));
            player.getTribe().activateTimedBuilding(this, player, this.board);
            board.getNextPlayerOnOfferTrack().ifPresentOrElse(this::setCurrPlayer,
                    () -> {
                            this.wakeUpWaiting();
                            this.phase.resolvePhase(this, this.board);
                    });
        }

    }

    /**
     * @return the ids of the cards currently visible on the board (present in one of the two rows)
     */
    public List<Integer> getVisibleIds() {
        List<Integer> ids = new ArrayList<>(this.idToCard.keySet());
        return ids.stream().filter(id -> this.board.containsCard(this.getCardById(id))).toList();
    }

    /** @return the associated GameObserver */
    public GameObserver getObserver() {
        return this.observer;
    }

    /**
     * Sends the full snapshot of the current game to the player who is reconnecting.
     */
    public void restartGame() {
        this.observer.broadcast(this.board.buildGameSnapshot(this.getAllPlayers(), this.currPlayer.getNickname(), this.phase));
    }

    /**
     * Removes a player from the game before it starts, returning their color to the pool.
     *
     * @param nickname the nickname of the player to remove
     */
    public void removePlayer(String nickname){
        try{
            this.availableColors.add(this.getPlayerByName(nickname).getColor());
            this.players.remove(this.getPlayerByName(nickname));
        }catch (Exception e){}

    }
}
