package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Artist;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.cards.Hunter;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class GameTest {
    private Game game;
    @BeforeAll
    void gameInitTest() throws Exception{
        game=new Game(2,"pippo", Color.BLACK);
        assertEquals(GamePhase.PREPARATION,game.getPhase());
        game.addPlayer("jonny", Color.RED);
        assertFalse(game.getAvailableColors().contains(Color.RED) || game.getAvailableColors().contains(Color.BLACK));
        assert game.getPhase().equals(GamePhase.OFFER_TRACK_SELECTION);
        assertEquals(2,game.getPlayers().size());
        List<String> namePlayers = new ArrayList<>();
        namePlayers.add("pippo");
        namePlayers.add("jonny");
        for(Player player : game.getPlayers()){
            assertTrue(namePlayers.contains(player.getNickname()));
        };
        assertNotNull(game.getCurrPlayer());
        assertTrue(namePlayers.contains(game.getCurrPlayer().getNickname()));
        for(int i=1;i<=117;i++){//just to test, then adjust
            assertNotNull(game.getCardById(i));
            assertEquals(i,game.getCardById(i).getId());
        }
    }

    @Test
    @Order(1)
    void placeTotemOnTrack() throws RuntimeException{
        Player player1= this.game.getCurrPlayer();
        this.game.placeTotemOnTrack(player1,3);
        Player player2= this.game.getCurrPlayer();
        this.game.placeTotemOnTrack(player2,2);
        assertEquals(GamePhase.ACTION_RESOLUTION,this.game.getPhase());
        assertEquals(player2,this.game.getCurrPlayer());
        ArrayList<OfferAction >actionsExpd1= new ArrayList<>();
        ArrayList<OfferAction >actionsExpd2= new ArrayList<>();
        actionsExpd1.add(OfferAction.TOP);
        actionsExpd1.add(OfferAction.TOP);
        actionsExpd2.add(OfferAction.BOTTOM);
        actionsExpd2.add(OfferAction.TOP);
        assertEquals(actionsExpd1,player1.getAvailableActionsActions());
        assertEquals(actionsExpd2,player2.getAvailableActionsActions());
    }

    @Test
    @Order(2)
    void pickCard(){
        Player player1 = this.game.getCurrPlayer();
        boolean notPicked=true;
        int i=1;
        while (notPicked){
            try {
                Card card= this.game.getCardById(i);
                this.game.pickCard(card,player1);
                notPicked=false;
            }catch (IllegalArgumentException e){
                System.out.println(e.getMessage()+i);
                i++;
            }
        }
        //to complete

    }





}