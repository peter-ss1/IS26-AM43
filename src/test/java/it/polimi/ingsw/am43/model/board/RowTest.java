package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.MockObserver;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RowTest {
    private Row row = new Row();

    @BeforeEach
    void setUp(){
        this.row = new Row();
    }

    @Test
    void size() {
        Building building = new TribeBuilding(1,1,1,1,new TribeBonus.FoodOnSet());
        CharacterCard characterCard = new Hunter(1,3,true);
        Event event = new HuntEvent(1,6);
        row.addCard(building);
        row.addCard(event);
        row.addCard(characterCard);
        assertEquals(3,row.size());
    }

    @Test
    void removeCharacters() {
        CharacterCard card = new Hunter(1,1,true);
        this.row.addCard(card);
        assertTrue(this.row.contains(card));
        this.row.removeCharacters();
        assertEquals(0, this.row.size());
    }

    @Test
    void removeBuildings() {
        Building card = new FinalBuilding(1,1,1,1,new FinalEffect.FinalBonusPrestigePoints());
        this.row.addCard(card);
        assertTrue(this.row.contains(card));
        this.row.removeBuildings();
        assertFalse(this.row.contains(card));
        assertEquals(0, this.row.size());
    }

    @Test
    void removeEvents() {
        Event card = new RitualEvent(1,1,1);
        this.row.addCard(card);
        assertTrue(this.row.contains(card));
        this.row.removeEvents();
        assertFalse(this.row.contains(card));
        assertEquals(0, this.row.size());
    }

    @Test
    void getAllCharacters() {
        CharacterCard  card1 = new Hunter(1,1,true);
        CharacterCard card2= new Artist(1,1);
        ArrayList<CharacterCard> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllCharacters(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list,this.row.getAllCharacters());
        this.row.removeCharacters();
        assertFalse(this.row.contains(card1) || this.row.contains(card2));
    }

    @Test
    void getAllBuildings() {
        Building  card1 = new FinalBuilding(1,1,1,1,new FinalEffect.FinalBonusPrestigePoints());
        Building card2= new FinalBuilding(1,1,1,1,new FinalEffect.FinalPrestigePointsByCharacterType(CharacterType.HUNTER,2));
        ArrayList<Building> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllBuildings(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list,this.row.getAllBuildings());
        this.row.removeBuildings();
        assertFalse(this.row.contains(card1) || this.row.contains(card2));
    }

    @Test
    void getAllEvents() {
        Event  card1 = new HuntEvent(2,2);
        Event card2= new RitualEvent(1,1,1);
        ArrayList<Event> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllEvents(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list,this.row.getAllEvents());
        assertFalse(!this.row.contains(card1) || !this.row.contains(card2));
    }

    @Test
    void addAllCharacters() {
        CharacterCard  card1 = new Hunter(1,1,true);
        CharacterCard card2= new Artist(1,1);
        ArrayList<CharacterCard> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllCharacters(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
    }

    @Test
    void addAllBuildings() {
        Building  card1 = new FinalBuilding(1,1,1,1,new FinalEffect.FinalBonusPrestigePoints());
        Building card2= new FinalBuilding(1,1,1,1,new FinalEffect.FinalPrestigePointsByCharacterType(CharacterType.HUNTER,2));
        ArrayList<Building> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllBuildings(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list,this.row.getAllBuildings());
        this.row.removeBuildings();
        assertFalse(this.row.contains(card1) || this.row.contains(card2));
    }

    @Test
    void addAllEvents() {
        Event  card1 = new HuntEvent(2,2);
        Event card2= new RitualEvent(1,1,1);
        ArrayList<Event> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        this.row.addAllEvents(list);
        assertTrue(this.row.contains(card1) && this.row.contains(card2));
        assertEquals(list,this.row.getAllEvents());
        this.row.removeEvents();
        assertFalse(this.row.contains(card1) || this.row.contains(card2));
    }

    @Test
    void contains() {
        CharacterCard card =new Artist(1,1);
        this.row.addCard(card);
        assertTrue(row.contains(card));

    }

    @Test
    void removeCard() {
        Event card = new RitualEvent(1,1,1);
        this.row.addCard(card);
        assertTrue(this.row.contains(card));
        this.row.removeCard(card);
        assertFalse(this.row.contains(card));
    }

    @Test
    void activateEvents() {
        row.activateEvents(new MockObserver(), new ArrayList<>());
    }

}