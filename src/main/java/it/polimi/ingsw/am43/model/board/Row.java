package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Row {

    private final List<CharacterCard> characters;
    private final List<Building> buildings;
    private final List<Event> events;

    public Row() {
        this.characters = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public int size() {
        return this.buildings.size() + this.characters.size() + this.events.size();
    }

    public void addCard(CharacterCard card) {
        this.characters.add(card);
    }

    public void addCard(Event event) {
        this.events.addFirst(event);
    }

    public void addCard(Building building) {//to talk later
        this.buildings.add(building);
    }

    public void addCard(SustenanceEvent sustenanceEvent) {
        this.events.addLast(sustenanceEvent);
    }

    public void removeCharacters() {
        this.characters.clear();
    }

    public void removeBuildings() {
        this.buildings.clear();
    }

    public void removeEvents() {
        this.events.clear();
    }

    public List<CharacterCard> getAllCharacters() {
        return new ArrayList<>(this.characters);
    }

    public List<Building> getAllBuildings() {
        return new ArrayList<>(this.buildings);
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(this.events);
    }

    public void addAllCharacters(List<CharacterCard> characters) {
        this.characters.addAll(characters);
    }

    public void addAllBuildings(List<Building> buildings) {
        this.buildings.addAll(buildings);
    }

    public void addAllEvents(List<Event> events) {
        this.events.addAll(events);
    }

    public boolean contains(CharacterCard character) {
        return this.characters.contains(character);
    }

    public boolean contains(Building building) {
        return buildings.contains(building);
    }

    public boolean contains(Event event) {
        return events.contains(event);
    }

    public void removeCard(CharacterCard character) throws IllegalArgumentException {
        if (!this.characters.remove(character)) throw new IllegalArgumentException("card not in row");
    }

    public void removeCard(Building building) throws IllegalArgumentException {
        if (!this.buildings.remove(building)) throw new IllegalArgumentException("card not in row");
    }

    public void removeCard(Event event) throws IllegalArgumentException {
        if (!this.events.remove(event)) throw new IllegalArgumentException("card not in row");
    }

    public void activateEvents(List<Player> p) {
        if (!this.events.isEmpty()) this.events.forEach(e -> e.affectPlayers(p));
    }

    public List<Integer> getIds() {
        return Stream.of(this.events, this.characters, this.buildings)
                .flatMap(Collection::stream)
                .map(Card::getId)
                .toList();
    }
}