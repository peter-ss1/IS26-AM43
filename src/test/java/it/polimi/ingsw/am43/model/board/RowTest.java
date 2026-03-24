package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Artist;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RowTest {
    private Row row = new Row();
    @Test
    void size() {
        row.size();
    }

    @Test
    void removeCharacters() {
        row.removeCharacters();
    }

    @Test
    void removeBuildings() {
        row.removeBuildings();
    }

    @Test
    void removeEvents() {
        row.removeEvents();
    }

    @Test
    void getAllCharacters() {
        row.getAllCharacters();
    }

    @Test
    void getAllBuildings() {
        row.getAllBuildings();
    }

    @Test
    void getAllEvents() {
        row.getAllEvents();
    }

    @Test
    void addAllCharacters() {
        row.addAllCharacters(new ArrayList<>());
    }

    @Test
    void addAllBuildings() {
        row.addAllBuildings(new ArrayList<>());
    }

    @Test
    void addAllEvents() {
        row.addAllEvents(new ArrayList<>());
    }

    @Test
    void contains() {
        row.contains(new Artist(1,1));
    }

    @Test
    void removeCard() {
        row.removeCard(new Artist(1,1));
    }

    @Test
    void activateEvents() {
        row.activateEvents(new ArrayList<>());
    }

    @Test
    void roundEndingRow() {
        row.roundEndingRow();
    }
}