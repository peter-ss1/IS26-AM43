package it.polimi.ingsw.am43.model.board;

import java.util.List;
import java.util.ArrayList;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.player.*;

public class Row {

    private final List<CharacterCard> characters;
    private final List<Building> buildings;
    private final List<Event> events;
    private final List<Event> eventResolutionQueue;

    public Row() {
        this.characters = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.events = new ArrayList<>();
        this.eventResolutionQueue = new ArrayList<>();
    }

    public int size(){
        return this.buildings.size() + this.characters.size() +this.events.size();
    }

    public void addCard(CharacterCard card) {
        this.characters.add(card);
    }
    public void addCard(Event event) {
        this.events.add(event);
    }
    public void addCard(Building building) {//to talk later
        this.buildings.add(building);
    }
    public void addCard(SustenanceEvent sustenanceEvent){
        this.events.addFirst(sustenanceEvent);
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

    public ArrayList<CharacterCard> getAllCharacters(){
        return new ArrayList<>(this.characters);
    }
    public ArrayList<Building> getAllBuildings(){
        return new ArrayList<>(this.buildings);
    }
    public ArrayList<Event> getAllEvents(){
        return new ArrayList<>(this.events);
    }

    public void addAllCharacters(ArrayList<CharacterCard> characters){
        this.characters.addAll(characters);
    }
    public void addAllBuildings(ArrayList<Building> buildings){
        this.buildings.addAll(buildings);
    }
    public void addAllEvents(ArrayList<Event> events){
        this.events.addAll(events);
    }

    public boolean contains(CharacterCard character){
        return this.characters.contains(character);
    }
    public boolean contains(Building building){
        return buildings.contains(building);
    }
    public boolean contains(Event event){
        return events.contains(event);
    }

    public void removeCard(CharacterCard character) throws IllegalArgumentException{
        if(!this.characters.remove(character)) throw new IllegalArgumentException("card not in row");
    }
    public void removeCard(Building building) throws IllegalArgumentException{
        if(!this.buildings.remove(building)) throw new IllegalArgumentException("card not in row");
    }
    public void removeCard(Event event) throws IllegalArgumentException{
        if(!this.events.remove(event)) throw new IllegalArgumentException("card not in row");
    }

    public void activateEvents(ArrayList<Player> p) {
        this.eventResolutionQueue.forEach(e->e.affectPlayers(p));
    }
    
}