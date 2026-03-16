package it.polimi.ingsw.am43.model.player;


import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.cards.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tribe {
    private Map<String, ArrayList<CharacterCard>> characters;
    private Map<String, ArrayList<Building>> buildings;

    public Tribe() {
        characters = new HashMap<>();
        for (String type : new String[]{"HUNTER", "INVENTOR", "BUILDER", "GATHERER", "SHAMAN", "ARTIST"}) {
            characters.put(type, new ArrayList<>());
        }
        buildings = new HashMap<>();
        for (String type : new String[]{"FINAL", "EVENT", "TRIBE", "TIMED"}) {
            buildings.put(type, new ArrayList<>());
        }
    }

    public void addCharacter(Hunter c) {
        characters.get("HUNTER").add(c);
    }

    public void addCharacter(Inventor c) {
        characters.get("INVENTOR").add(c);
    }

    public void addCharacter(Builder c) {
        characters.get("BUILDER").add(c);
    }

    public void addCharacter(Gatherer c) {
        characters.get("GATHERER").add(c);
    }

    public void addCharacter(Shaman c) {
        characters.get("SHAMAN").add(c);
    }

    public void addCharacter(Artist c) {
        characters.get("ARTIST").add(c);
    }

    // ── Aggiunta edifici ──────────────────────────────────────────────────────
    public void addBuilding(FinalBuilding b) {
        buildings.get("FINAL").add(b);
    }

    public void addBuilding(EventBuilding b) {
        buildings.get("EVENT").add(b);
    }

    public void addBuilding(TribeBuilding b) {
        buildings.get("TRIBE").add(b);
    }

    public void addBuilding(TimedBuilding b) {
        buildings.get("TIMED").add(b);
    }

    // ── Contatori ─────────────────────────────────────────────────────────────
    public int getTribeNumber() {
        return characters.values().stream().mapToInt(List::size).sum();
    }

    public int getHunterNumber() {
        return characters.get("HUNTER").size();
    }

    public int getInventorNumber() {
        return characters.get("INVENTOR").size();
    }

    public int getBuilderNumber() {
        return characters.get("BUILDER").size();
    }

    public int getGathererNumber() {
        return characters.get("GATHERER").size();
    }

    public int getArtistNumber() {
        return characters.get("ARTIST").size();
    }

    public int getShamanNumber() {
        return characters.get("SHAMAN").size();
    }

    // ── Attivazione edifici ───────────────────────────────────────────────────
    public void activateFinalBuildings(Player owner) {
        for (Building b : buildings.get("FINAL"))
            ((FinalBuilding) b).finalBuildingEffect(owner); //cast
    }

    public void activateEventBuildings(Event event, Player owner) {
        for (Building b : buildings.get("EVENT"))
            event.triggerBuilding((EventBuilding) b, owner);
    }

    public void activateTribeBuildings(Player owner) {
        for (Building b : buildings.get("TRIBE"))
            ((TribeBuilding) b).TribeBuildingEffect(owner);
    }

    public void activateTimedBuilding(Game game, Player owner) {
        for (Building b : buildings.get("TIMED"))
            ((TimedBuilding) b).buildingEffect();
    }

    // ── Metodi di punteggio ───────────────────────────────────────────────────

    /**
     * Restituisce il numero di set completi
     */
    public int getNumberOfSets() {
        int min = Integer.MAX_VALUE; //numeri enorm
        for (ArrayList<CharacterCard> list : characters.values())
            min = Math.min(min, list.size());
        return min == Integer.MAX_VALUE ? 0 : min; //se trova almeno uno min diverso da max value, altrimenti
        //non ha trovato nessuno e ritorna zero
    }

    /**
     * Restituisce il numero di coppie di Inventori con la stessa icona invenzione.
     */
    public int getInventorSymbolPairs() {
        Map<java.lang.Character, Integer> symbolCount = new HashMap<>();
        for (CharacterCard c : characters.get("INVENTOR")) {
            Inventor inv = (Inventor) c;
            symbolCount.merge(inv.getSymbol(), 1, Integer::sum);// se simbolo non c'era gia emtti uno alrimenti sommi uno
        }
        int pairs = 0;
        for (int count : symbolCount.values()) pairs += count / 2;
        return pairs;
    }

    /**
     * Restituisce il numero di icone invenzione distinte tra gli Inventori.
     */
    public int getDistinctInventorSymbols() {
        Set<java.lang.Character> symbols = new HashSet<>();
        for (Character c : characters.get("INVENTOR"))
            symbols.add(((Inventor) c).getSymbol());
        return symbols.size();
    }


}