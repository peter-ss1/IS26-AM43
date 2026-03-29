package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Artist;
import it.polimi.ingsw.am43.model.cards.Hunter;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    @BeforeEach
    void gameInitTest() throws Exception{
        game=new Game(5,"pippo7", Color.BLACK);
        assertEquals(GamePhase.PREPARATION,game.getPhase());
        game.addPlayer("jonny_0", Color.RED);
        game.addPlayer("rick", Color.CYAN);
        game.addPlayer("gandalf", Color.WHITE);
        game.addPlayer("frodo", Color.YELLOW);
        assertTrue(game.getAvailableColors().isEmpty());
        //assert game.getPhase().equals(GamePhase.OFFER_TRACK_SELECTION);
        assertEquals(5,game.getPlayers().size());
        List<String> namePlayers = new ArrayList<>();
        namePlayers.add("pippo7");
        namePlayers.add("jonny_0");
        namePlayers.add("rick");
        namePlayers.add("gandalf");
        namePlayers.add("frodo");
        for(Player player : game.getPlayers()){
            assertTrue(namePlayers.contains(player.getNickname()));
        };
        assertNotNull(game.getCurrPlayer());
        assertTrue(namePlayers.contains(game.getCurrPlayer().getNickname()));
        for(int i=1;i<=117;i++){
            assertNotNull(game.getCardById(i));
            assertEquals(i,game.getCardById(i).getId());
        }
    }



}