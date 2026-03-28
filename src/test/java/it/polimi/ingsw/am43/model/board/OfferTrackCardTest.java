package it.polimi.ingsw.am43.model.board;

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
        offerTrackCard = new OfferTrackCard(actions);
    }

    @Test
    void getActions() {
        offerTrackCard.getActions();
    }

    @Test
    void getPlayer() {
        offerTrackCard.getPlayer();
    }

    @Test
    void setPlayer() {
        offerTrackCard.setPlayer(new Player("1"));
    }

    @Test
    void removePlayer() {
        offerTrackCard.removePlayer();
    }
}