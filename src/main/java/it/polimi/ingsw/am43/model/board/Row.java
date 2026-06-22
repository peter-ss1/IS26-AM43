package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.cards.CharacterCard;
import it.polimi.ingsw.am43.model.cards.Event;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Stream;

/**
 * A single row of the board (top or bottom). Holds the three kinds of cards
 * separately: characters, buildings and events (the latter in a priority queue
 * so they are resolved in the correct order).
 */
public class Row implements Serializable {

    private final List<CharacterCard> characters;
    private final List<Building> buildings;
    private final PriorityQueue<Event> events;

    /** Creates an empty row. */
    public Row() {
        this.characters = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.events = new PriorityQueue<>();
    }

    /** @return the total number of cards in the row (characters + buildings + events) */
    public int size() {
        return this.buildings.size() + this.characters.size() + this.events.size();
    }

    /**
     * Adds a character card to the row.
     *
     * @param card the character to add
     */
    public void addCard(CharacterCard card) {
        this.characters.add(card);
    }

    /**
     * Adds an event card to the row.
     *
     * @param event the event to add
     */
    public void addCard(Event event) {
        this.events.add(event);
    }

    /**
     * Adds a building card to the row.
     *
     * @param building the building to add
     */
    public void addCard(Building building) {
        this.buildings.add(building);
    }

    /** Removes all character cards from the row. */
    public void removeCharacters() {
        this.characters.clear();
    }

    /** Removes all building cards from the row. */
    public void removeBuildings() {
        this.buildings.clear();
    }

    /** Removes all event cards from the row. */
    public void removeEvents() {
        this.events.clear();
    }

    /** @return a copy of the character cards in the row */
    public List<CharacterCard> getAllCharacters() {
        return new ArrayList<>(this.characters);
    }

    /** @return a copy of the building cards in the row */
    public List<Building> getAllBuildings() {
        return new ArrayList<>(this.buildings);
    }

    /** @return a copy of the event cards in the row */
    public List<Event> getAllEvents() {
        return new ArrayList<>(this.events);
    }

    /**
     * Adds all the given character cards to the row.
     *
     * @param characters the characters to add
     */
    public void addAllCharacters(List<CharacterCard> characters) {
        this.characters.addAll(characters);
    }

    /**
     * Adds all the given building cards to the row.
     *
     * @param buildings the buildings to add
     */
    public void addAllBuildings(List<Building> buildings) {
        this.buildings.addAll(buildings);
    }

    /**
     * Adds all the given event cards to the row.
     *
     * @param events the events to add
     */
    public void addAllEvents(List<Event> events) {
        this.events.addAll(events);
    }

    /**
     * @param character the character to look for
     * @return true if the character is in the row
     */
    public boolean contains(CharacterCard character) {
        return this.characters.contains(character);
    }

    /**
     * @param building the building to look for
     * @return true if the building is in the row
     */
    public boolean contains(Building building) {
        return buildings.contains(building);
    }

    /**
     * @param event the event to look for
     * @return true if the event is in the row
     */
    public boolean contains(Event event) {
        return events.contains(event);
    }

    /**
     * Removes the given character card from the row.
     *
     * @param character the character to remove
     * @throws IllegalArgumentException if the character is not in the row
     */
    public void removeCard(CharacterCard character) throws IllegalArgumentException {
        if (!this.characters.remove(character)) throw new IllegalArgumentException("card not in row");
    }

    /**
     * Removes the given building card from the row.
     *
     * @param building the building to remove
     * @throws IllegalArgumentException if the building is not in the row
     */
    public void removeCard(Building building) throws IllegalArgumentException {
        if (!this.buildings.remove(building)) throw new IllegalArgumentException("card not in row");
    }

    /**
     * Removes the given event card from the row.
     *
     * @param event the event to remove
     * @throws IllegalArgumentException if the event is not in the row
     */
    public void removeCard(Event event) throws IllegalArgumentException {
        if (!this.events.remove(event)) throw new IllegalArgumentException("card not in row");
    }

    /**
     * Resolves every event in the row in priority order, applying its effect to the players.
     *
     * @param observer the observer to notify of each effect
     * @param p        the list of all players
     */
    public void activateEvents(GameObserver observer, List<Player> p) {
        while (!this.events.isEmpty()) {
            Event e = this.events.poll();
            e.affectPlayers(observer, p);
        }
    }

    /** @return the ids of all cards in the row (events, characters and buildings) */
    public List<Integer> getIds() {
        return Stream.of(this.events, this.characters, this.buildings)
                .flatMap(Collection::stream)
                .map(Card::getId)
                .toList();
    }
}