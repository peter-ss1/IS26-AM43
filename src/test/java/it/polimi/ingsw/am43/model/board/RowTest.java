package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RowTest {
    private Row row = new Row();

    @BeforeEach
    void setUp() {
        this.row = new Row();
    }

    @Test
    void shouldShowTotalSize() {
        Building building = new TribeBuilding(1, 1, 1, 1, new TribeBonus.FoodOnSet());
        CharacterCard characterCard = new Hunter(1, 3, true);
        Event event = new HuntEvent(1, 6);
        row.addCard(building);
        row.addCard(event);
        row.addCard(characterCard);
        assertEquals(3, row.size());
    }

    @Test
    void shouldRemoveCharacterCardsFromRow() {
        CharacterCard card = new Hunter(1, 1, true);
        this.row.addCard(card);
        assertTrue(this.row.contains(card));
        this.row.removeCharacters();
        assertEquals(0, this.row.size());
    }

    @Test
    void shouldRemoveBuildingCardsFromRow() {
        Building card = new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints());
        this.row.addCard(card);
        assertTrue(this.row.contains(card));
        this.row.removeBuildings();
        assertFalse(this.row.contains(card));
        assertEquals(0, this.row.size());
    }

    @Test
    void shouldRemoveEventCardsFromRow() {
        Event card = new RitualEvent(1, 1, 1);
        this.row.addCard(card);
        assertTrue(this.row.contains(card));
        this.row.removeEvents();
        assertFalse(this.row.contains(card));
        assertEquals(0, this.row.size());
    }

    @Test
    void shouldReturnCharacterCardsFromRow() {
        CharacterCard card1 = new Hunter(1, 1, true);
        CharacterCard card2 = new Artist(1, 1);
        ArrayList<CharacterCard> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllCharacters(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list, this.row.getAllCharacters());
        this.row.removeCharacters();
        assertFalse(this.row.contains(card1) || this.row.contains(card2));
    }

    @Test
    void shouldReturnBuildingCardsFromRow() {
        Building card1 = new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints());
        Building card2 = new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalPrestigePointsByCharacterType(CharacterType.HUNTER, 2));
        ArrayList<Building> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllBuildings(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list, this.row.getAllBuildings());
        this.row.removeBuildings();
        assertFalse(this.row.contains(card1) || this.row.contains(card2));
    }

    @Test
    void shouldReturnEventCardsFromRow() {
        Event card1 = new HuntEvent(2, 2);
        Event card2 = new RitualEvent(1, 1, 1);
        Event card3 = new SustenanceEvent(1, 1);
        ArrayList<Event> list1 = new ArrayList<>();
        list1.add(card1);
        list1.add(card2);
        list1.add(card3);
        ArrayList<Event> list2 = new ArrayList<>();
        list2.add(card2);
        list2.add(card1);
        list2.add(card3);
        this.row.addAllEvents(list1);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list2, this.row.getAllEvents());
        assertFalse(!this.row.contains(card1) || !this.row.contains(card2));
    }

    @Test
    void shouldAddCharacterCardsToRow() {
        CharacterCard card1 = new Hunter(1, 1, true);
        CharacterCard card2 = new Artist(1, 1);
        ArrayList<CharacterCard> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllCharacters(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
    }

    @Test
    void shouldAddBuildingCardsToRow() {
        Building card1 = new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalBonusPrestigePoints());
        Building card2 = new FinalBuilding(1, 1, 1, 1, new FinalEffect.FinalPrestigePointsByCharacterType(CharacterType.HUNTER, 2));
        ArrayList<Building> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllBuildings(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list, this.row.getAllBuildings());
        this.row.removeBuildings();
        assertFalse(this.row.contains(card1) || this.row.contains(card2));
    }

    @Test
    void shouldAddEventCardsToRow() {
        Event card1 = new HuntEvent(2, 2);
        Event card2 = new RitualEvent(1, 1, 1);
        ArrayList<Event> list = new ArrayList<>();
        list.add(card2);
        list.add(card1);
        this.row.addAllEvents(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list, this.row.getAllEvents());
        this.row.removeEvents();
        assertFalse(this.row.contains(card1) || this.row.contains(card2));
    }

    @Test
    void shouldCheckCardPresence() {
        CharacterCard card = new Artist(1, 1);
        this.row.addCard(card);
        assertTrue(row.contains(card));

    }

    @Test
    void shouldRemoveCardFromRow() {
        Event card = new RitualEvent(1, 1, 1);
        this.row.addCard(card);
        assertTrue(this.row.contains(card));
        this.row.removeCard(card);
        assertFalse(this.row.contains(card));
    }
}