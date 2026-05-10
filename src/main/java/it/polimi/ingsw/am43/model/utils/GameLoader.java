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

public class GameLoader {

    private final ObjectMapper mapper;
    private final String pathConfig;
    private final Map<Integer,Card> idToCard;

    public  GameLoader(String pathConfig){
        this.mapper=new ObjectMapper();
        this.pathConfig= pathConfig;
        this.idToCard=new HashMap<>();;
    }

    public ArrayList<Integer> loadFoodModifiers(int numPlayers) throws IOException, IndexOutOfBoundsException{
         InputStream inputStream = getClass().getResourceAsStream(this.pathConfig);
         if (inputStream == null) throw new IOException("path json error");
         JsonNode orderQueueNode= mapper.readTree(inputStream).path("board").path("orderQueue");

         Map<Integer, ArrayList<Integer>> valueMap = mapper.convertValue(
                 orderQueueNode,
                 new TypeReference<Map<Integer, ArrayList<Integer>>>() {}
         );
        return valueMap.get(numPlayers);
    }

    public ArrayList<Card> loadTribeDeck(int numPlayer)throws IOException, IllegalArgumentException{
        InputStream inputStream = getClass().getResourceAsStream(this.pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode tribeDeckNode = mapper.readTree(inputStream).path("board").path("tribeDeck");
        if (tribeDeckNode == null) throw new IOException("node json error");

        List<TribeDeckCardDTO> tribeDeckDTO = mapper.convertValue(
                tribeDeckNode,
                new TypeReference<ArrayList<TribeDeckCardDTO>>() {}
        );
        long seed = loadSeed();
        Collections.shuffle(tribeDeckDTO, new Random(seed));
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

    public Map<Integer, List<Building>> loadBuildingDeck(int numPlayer)throws IOException, IllegalArgumentException{
        InputStream inputStream = getClass().getResourceAsStream(this.pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode buildingDeckNode = mapper.readTree(inputStream).path("board").path("buildingDeck");
        if (buildingDeckNode == null) throw new IOException("node json error");

        Map<Integer,List<BuildingDeckCardDTO>> buildingDeckTDO= mapper.convertValue(
                buildingDeckNode,
                new TypeReference<Map<Integer, List<BuildingDeckCardDTO>>>() {}
        );
        long seed= this.loadSeed();
        ArrayList<Integer> numBuildings= this.loadNumBuildings(numPlayer);
        Map<Integer,List<Building>> buildingDeck = new HashMap<Integer, List<Building>>();
        for (Integer i: buildingDeckTDO.keySet()){
            buildingDeck.put(i,new ArrayList<Building>());
            Collections.shuffle(buildingDeckTDO.get(i),new Random(seed));
            buildingDeckTDO.get(i).subList(numBuildings.get(i-1),buildingDeckTDO.get(i).size()).clear();
            for (BuildingDeckCardDTO tdo : buildingDeckTDO.get(i)){
                Building newBuilding= tdo.createBuilding();
                buildingDeck.get(i).add(newBuilding);
                this.idToCard.put(newBuilding.getId(),newBuilding);
            }
        }
        return buildingDeck;
    }

    public Map<Integer,Card> loadIdToCardMap(){
        return new HashMap<>(this.idToCard);
    }

    public ArrayList<OfferTrackCard> loadOfferTrackCard(int numPlayers) throws IOException, IllegalArgumentException{
        InputStream inputStream = getClass().getResourceAsStream(this.pathConfig);
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

    private Long loadSeed()throws IOException{
        InputStream inputStream = getClass().getResourceAsStream(this.pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode seedNode = mapper.readTree(inputStream).path("board").path("seed");
        return  seedNode.asLong(1234);
    }

    private ArrayList<Integer> loadNumBuildings(int numPlayers) throws IOException, IndexOutOfBoundsException{
        InputStream inputStream = getClass().getResourceAsStream(this.pathConfig);
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
