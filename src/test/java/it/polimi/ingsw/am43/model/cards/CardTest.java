package it.polimi.ingsw.am43.model.cards;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void eraTest() {
        Card card = new Artist(2);
        assertEquals(2, card.getEra());
    }
}