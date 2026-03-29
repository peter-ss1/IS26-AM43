package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Artist;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void setPlayerOnTrack() {
        Player player = new Player("pippo", Color.RED);
        board.setPlayerOnTrack(player,3);
    }


    @Test
    void idBuildingCardMap() {
        //board.idBuildingCardMap();
    }

    @Test
    void getOfferTrackCardActions() {
        //board.getOfferTrackCardActions(3);
    }

    @Test
    void getCurrEra() {
        board.getCurrEra();
    }

    @Test
    void increaseCurrEra() {
        //board.increaseCurrEra();
    }

    @Test
    void replenishTopRow() {
        //board.replenishTopRow(3);
    }

    @Test
    void getAvailableActions() {
        board.getAvailableActions(new Player("1", Color.RED));
    }

    @Test
    void getCardPosition() {
        //board.getCardPosition(new Artist(1, 1));
    }

    @Test
    void removeAvailableAction() {
        //board.removeAvailableAction(new Player("1"), OfferAction.TOP);
    }

    @Test
    void removeCard() {
        //board.removeCard(new Artist(1, 1));
    }

    @Test
    void getNextPlayerInOrderQueue() {
        //board.getNextPlayerInOrderQueue();
    }

    @Test
    void getNextOccupiedOfferTrackCard() {
        //board.getNextOccupiedOfferTrackCard();
    }

    @Test
    void activateEvents() {
       // board.activateEvents(new ArrayList<>());
    }

    @Test
    void moveTopToBottomTribe() {
        //board.moveTopToBottomTribe();
    }

    @Test
    void moveTopToBottomBuildings() {
        //board.moveTopToBottomBuildings();
    }

    @Test
    void returnPlayerToOrderQueue() {
        //board.returnPlayerToOrderQueue(new Player("1"));
    }
}