package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OfferTrackCardTest {
    private OfferTrackCard offerTrackCard;

    @BeforeEach
    void setUp() {
        ArrayList<OfferAction> actions=new ArrayList<OfferAction>();
        actions.add(OfferAction.BOTTOM);
        actions.add(OfferAction.TOP);
        offerTrackCard = new OfferTrackCard(actions);
    }

    @Test
    void getActions() {
        ArrayList<OfferAction> actions=new ArrayList<OfferAction>();
        actions.add(OfferAction.BOTTOM);
        actions.add(OfferAction.TOP);
        assertEquals(actions,this.offerTrackCard.getActions());
    }

    @Test
    void getPlayer() {
        Player player = new Player("pippo",Color.WHITE);
        assertFalse(this.offerTrackCard.getPlayer().isPresent());
        this.offerTrackCard.setPlayer(player);
        assertTrue(this.offerTrackCard.getPlayer().isPresent());
        assertEquals(player,this.offerTrackCard.getPlayer().get());
    }

    @Test
    void setPlayer() {
        Player player = new Player("pippo",Color.WHITE);
        assertFalse(this.offerTrackCard.getPlayer().isPresent());
        this.offerTrackCard.setPlayer(player);
        assertTrue(this.offerTrackCard.getPlayer().isPresent());
        assertEquals(player,this.offerTrackCard.getPlayer().get());
    }

    @Test
    void removePlayer() {
        Player player = new Player("pippo",Color.WHITE);
        assertFalse(this.offerTrackCard.getPlayer().isPresent());
        this.offerTrackCard.setPlayer(player);
        assertTrue(this.offerTrackCard.getPlayer().isPresent());
        assertEquals(player,this.offerTrackCard.getPlayer().get());
        this.offerTrackCard.removePlayer();
        assertTrue(this.offerTrackCard.getPlayer().isEmpty());

    }
}