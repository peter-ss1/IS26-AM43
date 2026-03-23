package it.polimi.ingsw.am43;

import it.polimi.ingsw.am43.model.board.Row;
import it.polimi.ingsw.am43.model.cards.CharacterCard;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterCardTest {
    private static class DummyCharacterCard extends CharacterCard {
        public DummyCharacterCard(int era) {
            super(era);
        }

        @Override
        public void tribeEntranceEffect(Player player) {}
        }

        @Test
        void constructorShouldStoreEra() {
            DummyCharacterCard card = new DummyCharacterCard(2);

            assertEquals(2, card.getEra());
        }

        @Test
        void isPickableShouldAlwaysReturnTrue() {
            DummyCharacterCard card = new DummyCharacterCard(1);

            assertTrue(card.isPickable());
        }

        @Test
        void firstRowChoiceShouldAlwaysReturnBottom() {
            DummyCharacterCard card = new DummyCharacterCard(3);

            assertEquals(OfferAction.BOTTOM, card.firstRowChoice());
        }

        /*@Test
        void addToRowShouldAddCardToRowsCharacterCollection() {
            DummyCharacterCard card = new DummyCharacterCard(1);
            Row row = new Row();

            card.addToRow(row);

            assertEquals(1, row.getCharacters().size());
            assertSame(card, row.getCharacters().get(0));
        }*/

}
