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

public class Game implements ModelInterface, Serializable {
    private final List<Color> availableColors;
    private final List<Player> players;
    private final int numPlayers;
    private Player currPlayer;
    private GamePhase phase;
    private Board board;
    private final Map<Integer, Card> idToCard;
    private transient GameObserver observer;


    public Game(int np, String nk, Color color) {
        this.availableColors = new ArrayList<>(Arrays.asList(Color.values()));
        this.numPlayers = np;
        this.phase = GamePhase.PREPARATION;
        this.players = new ArrayList<>();
        this.idToCard = new HashMap<>();
        this.addPlayer(nk, color);
    }

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

    public void moveToWait(Player player) throws IllegalArgumentException{
        if (!player.getStatus().equals(PlayerStatus.INACTIVE)) throw new IllegalArgumentException("player not inactive");
        player.setStatus(PlayerStatus.WAITING);
        this.observer.updatePlayer(player.getNickname(),this.board.buildGameSnapshot(this.getAllPlayers(), this.currPlayer.getNickname(), this.phase));
    }

    public void wakeUpWaiting() {
        this.players.stream().filter(p->p.getStatus().equals(PlayerStatus.WAITING)).forEach(p->{
            p.setStatus(PlayerStatus.ACTIVE);
            this.board.addPlayerFromBoard();
            this.board.returnPlayerToOrderQueue(this.observer,p);
        });
    }

    public void setObserver(GameObserver observer) {
        this.observer = observer;
    }

    public ArrayList<Color> getAvailableColors() {
        return new ArrayList<>(this.availableColors);
    }

    public void initBoard(List<Player> players, int numPlayers, List<Integer> foodModifiers, List<Card> tribeDeck, Map<Integer, List<Building>> buildingDeck, List<OfferTrackCard> offerTrack) throws RuntimeException {
        this.board = new Board(players, numPlayers, foodModifiers, tribeDeck, buildingDeck, offerTrack);
        this.currPlayer = this.board.getNextPlayerInOrderQueue();
    }

    public void setIdCard(Map<Integer, Card> map) {
        this.idToCard.putAll(map);
    }

    public ArrayList<Player> getAllPlayers(){
        return new ArrayList<>(this.players);
    }

    public ArrayList<Player> getActivePlayers() {
        return new ArrayList<>(this.players.stream().filter(p->p.getStatus().equals(PlayerStatus.ACTIVE)).toList());
    }

    public ArrayList<Player> getInactivePlayers() {
        return new ArrayList<>(this.players.stream().filter(p->p.getStatus().equals(PlayerStatus.INACTIVE)).toList());
    }

    public ArrayList<Player> getWaitingPlayers() {
        return new ArrayList<>(this.players.stream().filter(p->p.getStatus().equals(PlayerStatus.WAITING)).toList());
    }

    @Override
    public void startGame() {
        this.getPhase().resolvePhase(this, this.board);
        this.board.buildGameStartedUpdate(this.observer, this.players, this.currPlayer.getNickname());
    }

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

    public int getNumPlayers() {
        return this.numPlayers;
    }

    public Player getCurrPlayer() {
        return this.currPlayer;
    }

    public void setCurrPlayer(Player player) {
        if (!this.players.contains(player))
            throw new IllegalArgumentException(("Player is not registered in the game"));
        this.currPlayer = player;
        this.observer.broadcast(new Update.CurrentPlayerUpdate(this.currPlayer.getNickname()));
    }

    public GamePhase getPhase() {
        return this.phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
        this.observer.broadcast(new Update.NewPhaseUpdate(this.phase));
    }

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

    public Player getPlayerByName(String name) {
        for (Player p : this.players) if (p.getNickname().equals(name)) return p;
        throw new IllegalArgumentException("Player is not registered in the game");

    }

    public Card getCardById(int id) throws IllegalArgumentException {
        if (!this.idToCard.containsKey(id)) throw new IllegalArgumentException("Card is not registered in the game");
        return this.idToCard.get(id);
    }

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

    public List<Integer> getVisibleIds() {
        List<Integer> ids = new ArrayList<>(this.idToCard.keySet());
        return ids.stream().filter(id -> this.board.containsCard(this.getCardById(id))).toList();
    }

    public GameObserver getObserver() {
        return this.observer;
    }

    public void restartGame() {
        this.observer.broadcast(this.board.buildGameSnapshot(this.getAllPlayers(), this.currPlayer.getNickname(), this.phase));
    }

    public void removePlayer(String nickname){
        try{
            this.availableColors.add(this.getPlayerByName(nickname).getColor());
            this.players.remove(this.getPlayerByName(nickname));
        }catch (Exception e){}

    }
}
