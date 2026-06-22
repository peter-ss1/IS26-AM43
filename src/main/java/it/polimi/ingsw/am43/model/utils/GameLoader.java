package it.polimi.ingsw.am43.model.utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import it.polimi.ingsw.am43.model.board.OfferTrackCard;
import it.polimi.ingsw.am43.model.cards.Building;
import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.cards.Card.*;
import it.polimi.ingsw.am43.model.cards.TribeCard;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.utils.DTOs.BuildingDeckCardDTO;
import it.polimi.ingsw.am43.model.utils.DTOs.TribeDeckCardDTO;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads the game configuration from the bundled {@code config.json} resource and
 * builds the decks, the offer track and the board parameters. Card creation is
 * driven by a fixed seed so that shuffles are reproducible.
 */
public class GameLoader {
    private static final String pathConfig = "/it/polimi/ingsw/am43/config.json";

    private final ObjectMapper mapper;
    private final Map<Integer,Card> idToCard;
    private final long seed;

    /**
     * @param seed the seed used to make all the deck shuffles reproducible
     */
    public  GameLoader(long seed){
        this.mapper=new ObjectMapper();
        this.idToCard=new HashMap<>();
        this.seed = seed;
    }

    /**
     * Loads the food modifiers of the order queue for the given number of players.
     *
     * @param numPlayers the number of players in the game
     * @return the ordered list of food modifiers
     * @throws IOException               if the configuration resource cannot be read
     * @throws IndexOutOfBoundsException if there is no entry for the given player count
     */
    public ArrayList<Integer> loadFoodModifiers(int numPlayers) throws IOException, IndexOutOfBoundsException{
         InputStream inputStream = getClass().getResourceAsStream(pathConfig);
         if (inputStream == null) throw new IOException("path json error");
         JsonNode orderQueueNode= mapper.readTree(inputStream).path("board").path("orderQueue");

         Map<Integer, ArrayList<Integer>> valueMap = mapper.convertValue(
                 orderQueueNode,
                 new TypeReference<Map<Integer, ArrayList<Integer>>>() {}
         );
        return valueMap.get(numPlayers);
    }

    /**
     * Loads, shuffles (by seed) and orders the tribe deck, keeping only the cards
     * compatible with the given number of players. Created cards are registered in
     * the id-to-card map.
     *
     * @param numPlayer the number of players in the game
     * @return the ordered tribe deck
     * @throws IOException              if the configuration resource cannot be read
     * @throws IllegalArgumentException if the configuration data is malformed
     */
    public ArrayList<Card> loadTribeDeck(int numPlayer)throws IOException, IllegalArgumentException{
        InputStream inputStream = getClass().getResourceAsStream(pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode tribeDeckNode = mapper.readTree(inputStream).path("board").path("tribeDeck");
        if (tribeDeckNode == null) throw new IOException("node json error");

        List<TribeDeckCardDTO> tribeDeckDTO = mapper.convertValue(
                tribeDeckNode,
                new TypeReference<ArrayList<TribeDeckCardDTO>>() {}
        );
        Collections.shuffle(tribeDeckDTO, new Random(this.seed));
        tribeDeckDTO.sort(Comparator.comparingInt((TribeDeckCardDTO dto)->dto.era).thenComparing((TribeDeckCardDTO dto)->dto.isFinal));
        ArrayList<Card> tribeDeck= new ArrayList<Card>();
        for(TribeDeckCardDTO tdo : tribeDeckDTO){
            if (tdo.minPlayer!=null && tdo.minPlayer>numPlayer)continue;
            Card newCard= tdo.createCard();
            tribeDeck.add(newCard);
            this.idToCard.put(newCard.getId(),newCard);
        }
        return tribeDeck;
    }

    /**
     * Loads the building deck grouped by era, shuffling each era (by seed) and
     * trimming it to the number of buildings allowed for the given player count.
     * Created buildings are registered in the id-to-card map.
     *
     * @param numPlayer the number of players in the game
     * @return a map from era to its list of buildings
     * @throws IOException              if the configuration resource cannot be read
     * @throws IllegalArgumentException if the configuration data is malformed
     */
    public Map<Integer, List<Building>> loadBuildingDeck(int numPlayer)throws IOException, IllegalArgumentException{
        InputStream inputStream = getClass().getResourceAsStream(pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode buildingDeckNode = mapper.readTree(inputStream).path("board").path("buildingDeck");
        if (buildingDeckNode == null) throw new IOException("node json error");

        Map<Integer,List<BuildingDeckCardDTO>> buildingDeckTDO= mapper.convertValue(
                buildingDeckNode,
                new TypeReference<Map<Integer, List<BuildingDeckCardDTO>>>() {}
        );
        ArrayList<Integer> numBuildings= this.loadNumBuildings(numPlayer);
        Map<Integer,List<Building>> buildingDeck = new HashMap<Integer, List<Building>>();
        for (Integer i: buildingDeckTDO.keySet()){
            buildingDeck.put(i,new ArrayList<Building>());
            Collections.shuffle(buildingDeckTDO.get(i),new Random(this.seed));
            buildingDeckTDO.get(i).subList(numBuildings.get(i-1),buildingDeckTDO.get(i).size()).clear();
            for (BuildingDeckCardDTO tdo : buildingDeckTDO.get(i)){
                Building newBuilding= tdo.createBuilding();
                buildingDeck.get(i).add(newBuilding);
                this.idToCard.put(newBuilding.getId(),newBuilding);
            }
        }
        return buildingDeck;
    }

    /**
     * @return a copy of the map associating each card id with its card; populated
     *         while loading the tribe and building decks
     */
    public Map<Integer,Card> loadIdToCardMap(){
        return new HashMap<>(this.idToCard);
    }

    /**
     * Loads the offer track cards, removing the entries not used for the given
     * number of players.
     *
     * @param numPlayers the number of players in the game
     * @return the list of offer track cards
     * @throws IOException              if the configuration resource cannot be read
     * @throws IllegalArgumentException if the configuration data is malformed
     */
    public ArrayList<OfferTrackCard> loadOfferTrackCard(int numPlayers) throws IOException, IllegalArgumentException{
        InputStream inputStream = getClass().getResourceAsStream(pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode offerTrackNode = mapper.readTree(inputStream).path("board").path("offerTrack");
        if (offerTrackNode == null) throw new IOException("node json error");

        ArrayList<ArrayList<OfferAction>> offerTrackBone= mapper.convertValue(
                offerTrackNode,
                new TypeReference<ArrayList<ArrayList<OfferAction>>>() {}
        );
        if(numPlayers<5){
            offerTrackBone.removeFirst();
            if(numPlayers<4){
                offerTrackBone.removeLast();
                if(numPlayers<3)
                    offerTrackBone.remove(2);
            }
        }

        ArrayList<OfferTrackCard> offerTrack = new ArrayList<OfferTrackCard>();
        for(ArrayList<OfferAction> actions: offerTrackBone){
            offerTrack.add(new OfferTrackCard(actions));
        }
        return offerTrack;
    }

    private ArrayList<Integer> loadNumBuildings(int numPlayers) throws IOException, IndexOutOfBoundsException{
        InputStream inputStream = getClass().getResourceAsStream(pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode numBuildings = mapper.readTree(inputStream).path("board").path("numBuildingsByNumPlayers");
        if (numBuildings == null) throw new IOException("node json error");
        Map<Integer,ArrayList<Integer>> map= mapper.convertValue(
                numBuildings,
                new TypeReference<Map<Integer, ArrayList<Integer>>>() {}
        );
        return map.get(numPlayers);
    }



}
