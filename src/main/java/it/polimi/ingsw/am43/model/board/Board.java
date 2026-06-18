package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.client.ClientPlayer;
import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.player.Tribe;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Board implements Serializable {

    private final OrderQueue turnOrder;
    private final TribeDeck tribeDeck;
    private final BuildingDeck buildingDeck;
    private final Row topRow;
    private final Row bottomRow;
    private final List<OfferTrackCard> offerTrack;
    private int currEra;
    private int playersOnBoard;

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

    public void addPlayerFromBoard(){
        this.playersOnBoard++;
    }

    public int getCurrEra() {
        return this.currEra;
    }

    public void setPlayerOnTrack(Player player, int position) throws IndexOutOfBoundsException, IllegalMoveException {
        if (position < 0 || position > this.offerTrack.size()) throw new IndexOutOfBoundsException("Invalid position");
        if (this.offerTrack.get(position).getPlayer().isPresent())
            throw new IllegalMoveException("Cannot pick occupied tile");
        this.offerTrack.get(position).setPlayer(player);
    }

    public void increaseCurrEra() {
        if (++this.currEra > 3) throw new IllegalStateException("Era does not exist");
        this.bottomRow.removeBuildings();
        this.moveTopToBottomBuildings();
        this.topRow.addAllBuildings(this.buildingDeck.revealEra(this.currEra));
    }

    public void replenishTopRow(int amount) {
        for (int i = 0; i < amount; i++) {
            Card card = this.tribeDeck.draw();
            card.addToRow(this.topRow);
            if (card.getEra() > this.currEra) {
                this.increaseCurrEra();
            }
        }
    }

    public OfferAction getCardPosition(Card card) {
        if (card.isContainedInRow(this.topRow)) return OfferAction.TOP;
        if (card.isContainedInRow(this.bottomRow)) return OfferAction.BOTTOM;
        throw new IllegalArgumentException("card not in board");
    }

    public void removeCard(Card card) {
        if (card.isContainedInRow(this.topRow)) {
            card.removeFromRow(this.topRow);
            return;
        }
        if (card.isContainedInRow(this.bottomRow)) {
            card.removeFromRow(this.bottomRow);
        }
    }

    public Player getNextPlayerInOrderQueue() {
        return this.turnOrder.peek();
    }

    public void popNextPlayerInOrderQueue() {
        this.turnOrder.pop();
    }

    public Optional<Player> getNextPlayerOnOfferTrack() {
        for (OfferTrackCard otd : this.offerTrack) {
            if (otd.getPlayer().isPresent()) return otd.getPlayer();
        }
        return Optional.empty();
    }

    public boolean containsCard(Card card) {
        return card.isContainedInRow(this.topRow) || card.isContainedInRow(this.bottomRow);
    }

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

    public void moveTopToBottomTribe() {
        this.bottomRow.removeCharacters();
        this.bottomRow.removeEvents();
        this.bottomRow.addAllCharacters(this.topRow.getAllCharacters());
        this.bottomRow.addAllEvents(this.topRow.getAllEvents());
        this.topRow.removeCharacters();
        this.topRow.removeEvents();
    }

    public void moveTopToBottomBuildings() {
        this.bottomRow.addAllBuildings(this.topRow.getAllBuildings());
        this.topRow.removeBuildings();
    }

    public void returnPlayerToOrderQueue(GameObserver observer, Player player) {
        for (OfferTrackCard otd : this.offerTrack) {
            if (otd.getPlayer().isPresent() && otd.getPlayer().get().equals(player)) {
                otd.removePlayer();
                break;
            }
        }
        this.turnOrder.append(observer, player);
        //TODO boolean on modifiers
    }

    public boolean isOrderQueueEmpty() {
        return this.turnOrder.isEmpty();
    }

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

    public boolean checkFinalRound() { return tribeDeck.isEmpty();
    }

    public boolean isOrderQueueFull() {
        return turnOrder.size()==this.playersOnBoard;
    }//TODO res

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

    public boolean hasFoodBonus() {
        return turnOrder.getLastFoodGiven() > 0;
    }

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

    public void buildNewRoundUpdate(GameObserver observer) {
        observer.broadcast(new Update.NewRoundUpdate(this.currEra, this.topRow.getIds(), this.bottomRow.getIds()));
    }


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
