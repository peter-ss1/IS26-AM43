package it.polimi.ingsw.am43.model.player;

import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.utils.GameObserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tribe implements Serializable {


    // Characters

    private final List<Hunter> hunters;
    private final List<Inventor> inventors;
    private final List<Builder> builders;
    private final List<Gatherer> gatherers;
    private final List<Artist> artists;
    private final List<Shaman> shaman;


    // Buildings

    private final List<FinalBuilding>  finalBuildings;
    private final List<EventBuilding>  eventBuildings;
    private final List<TribeBuilding>  tribeBuildings;
    private final List<TimedBuilding>  timedBuildings;

    public Tribe() {
        hunters=new ArrayList<>();
        inventors=new ArrayList<>();
        builders=new ArrayList<>();
        gatherers=new ArrayList<>();
        artists=new ArrayList<>();
        shaman=new ArrayList<>();
        finalBuildings=new ArrayList<>();
        eventBuildings=new ArrayList<>();
        tribeBuildings=new ArrayList<>();
        timedBuildings=new ArrayList<>();
    }

    public void addCardToTribe(Hunter c){
        hunters.add(c);
    }
    public void addCardToTribe(Inventor c){
        inventors.add(c);
    }
    public void addCardToTribe(Builder c){
        builders.add(c);
    }
    public void addCardToTribe(Gatherer c){
        gatherers.add(c);
    }
    public void addCardToTribe(Artist c){
        artists.add(c); }
    public void addCardToTribe(Shaman c){
        shaman.add(c);
    }
    public void addCardToTribe(FinalBuilding c){
        finalBuildings.add(c);
    }
    public void addCardToTribe(EventBuilding c){
        eventBuildings.add(c);
    }
    public void addCardToTribe(TribeBuilding c){
        tribeBuildings.add(c);
    }
    public void addCardToTribe(TimedBuilding c){
        timedBuildings.add(c);
    }






    /**
     *ridà numero character in tribe
     */
    public int getTribeNumber() {
        return hunters.size()
                + inventors.size()
                + builders.size()
                + gatherers.size()
                + artists.size()
                + shaman.size();
    }

    /**
     * da il numero di character di un certo tipo
     */
    public int getNumberByCharacterType(CharacterType type) {
        switch (type) {
            case HUNTER:
                return hunters.size();
            case INVENTOR:
                return inventors.size();
            case BUILDER:
                return builders.size();
            case GATHERER:
                return gatherers.size();
            case ARTIST:
                return artists.size();
            case SHAMAN:
                return shaman.size();
            default:
                throw new IllegalArgumentException("Unknown CharacterType: " + type);
        }
    }


    public void activateFinalBuildings(Player player) {
        for (FinalBuilding fb : finalBuildings) {
            fb.finalBuildingEffect(player);
        }
    }


    public void activateEventBuildings(GameObserver observer, Event event, Player player) {
        for (EventBuilding eb : eventBuildings) {
            event.triggerBuilding(observer, eb, player);
        }
    }


    public void activateTribeBuildings(GameObserver observer, Player player) {
        for (TribeBuilding tb : tribeBuildings) {
            tb.tribeBuildingEffect(observer, player);
        }
    }


    public void activateTimedBuilding(Game game, Player player, Board board) {
        for (TimedBuilding tb : timedBuildings) {
            tb.TimedBuildingEffect(player, game, board);
        }
    }




    public int getNumberOfSets() {
        return Math.min(
                hunters.size(),
                Math.min(
                        inventors.size(),
                        Math.min(
                                builders.size(),
                                Math.min(
                                        gatherers.size(),
                                        Math.min(
                                                artists.size(),
                                                shaman.size()
                                        )
                                )
                        )
                )
        );
    }


    public int getInventorSymbolPairs() {
        Map<InventorSymbol, Integer> counts = getSymbolCounts();
        int pairs = 0;
        for (int count : counts.values()) {
            pairs += count / 2;
        }
        return pairs;
    }


    public int getDistinctInventorSymbols() {
        return getSymbolCounts().size();
    }


    private Map<InventorSymbol, Integer> getSymbolCounts() {
        Map<InventorSymbol, Integer> counts = new HashMap<>();  //mappa per contare quante volte simbolo appare
        for (Inventor inv : inventors) {
            InventorSymbol sym = inv.getSymbol();
            counts.put(sym, counts.getOrDefault(sym, 0) + 1);
        }
        return counts;
    }



    public int getBuildersTotalPrestigePoints() {
        int total = 0;
        for (Builder b : builders) {
            total += b.getPrestigePoints();
        }
        return total;
    }

    public int getBuildingsTotalPrestigePoints() {
        int total = 0;
        for (FinalBuilding fb : finalBuildings)  total += fb.getPrestigePoints();
        for (EventBuilding eb : eventBuildings)  total += eb.getPrestigePoints();
        for (TribeBuilding tb : tribeBuildings)  total += tb.getPrestigePoints();
        for (TimedBuilding tb : timedBuildings)  total += tb.getPrestigePoints();
        return total;
    }



}