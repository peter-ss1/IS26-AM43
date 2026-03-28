package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Artist;
import it.polimi.ingsw.am43.model.cards.Hunter;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        try {
            game = new Game(2, "1");
            //game.addPlayer("2");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAvailableColors() {
        game.getAvailableColors();
    }

    @Test
    void pickColor() {
        game.pickColor("1", Color.BLACK);
    }

    @Test
    void getPlayers() {
        game.getPlayers();
    }

    @Test
    void getNumPlayers() {
        assertEquals(2, game.getNumPlayers());
    }

    @Test
    void setCurrPlayer() {
        game.setCurrPlayer(game.getPlayerByName("1"));
        assertEquals(game.getPlayerByName("1"), game.getCurrPlayer());
    }

    @Test
    void getPhase() {
        assertEquals(GamePhase.PREPARATION, game.getPhase());
    }

    @Test
    void setPhase() {
        game.setPhase(GamePhase.OFFER_TRACK_SELECTION);
        assertEquals(GamePhase.OFFER_TRACK_SELECTION, game.getPhase());
    }

    @Test
    void placeTotemOnTrack() {
        //game.placeTotemOnTrack("1", 3);
    }

    @Test
    void getPlayerByName() {
        game.getPlayerByName("1");
    }

    @Test
    void getCardById() {
        game.getCardById(1);
    }

    @Test
    void getBuildingById() {
        game.getBuildingById(1);
    }

    @Test
    void pickCard() {
        //game.pickCard(new Artist(1, 1), new Player("2"));
    }

    @Test
    void endCurrentTurn() {
        game.endCurrentTurn(new Player("1"));
    }

    @Test
    void resolveOffer() {
        game.resolveOffer();
    }
}