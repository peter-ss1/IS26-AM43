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

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GameLoader {

    private final ObjectMapper mapper;
    private final String pathConfig;
    private final Map<Integer,Card> idToCard;

    public  GameLoader(String pathConfig){
        this.mapper=new ObjectMapper();
        this.pathConfig= pathConfig;
        this.idToCard=new HashMap<>();
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

    public ArrayList<Card> loadTribeDeck()throws IOException, IllegalArgumentException{
        InputStream inputStream = getClass().getResourceAsStream(this.pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode tribeDeckNode = mapper.readTree(inputStream).path("board").path("tribeDeck");
        if (tribeDeckNode == null) throw new IOException("node json error");

        List<TribeDeckCardDTO> tribeDeckDTO = mapper.convertValue(
                tribeDeckNode,
                new TypeReference<ArrayList<TribeDeckCardDTO>>() {}
        );
        ArrayList<Card> tribeDeck= new ArrayList<Card>();
        for(TribeDeckCardDTO tdo : tribeDeckDTO){
            Card newCard= tdo.createCard();
            tribeDeck.add(newCard);
            this.idToCard.put(newCard.getId(),newCard);
        }
        return tribeDeck;
    }

    public Map<Integer,ArrayList<Building>> loadBuildingDeck()throws IOException, IllegalArgumentException{
        InputStream inputStream = getClass().getResourceAsStream(this.pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode buildingDeckNode = mapper.readTree(inputStream).path("board").path("buildingDeck");
        if (buildingDeckNode == null) throw new IOException("node json error");

        Map<Integer,ArrayList<BuildingDeckCardDTO>> buildingDeckTDO= mapper.convertValue(
                buildingDeckNode,
                new TypeReference<Map<Integer, ArrayList<BuildingDeckCardDTO>>>() {}
        );
        Map<Integer,ArrayList<Building>> buildingDeck = new HashMap<Integer,ArrayList<Building>>();
        for(Integer i : buildingDeckTDO.keySet()){
            buildingDeck.put(i,new ArrayList<Building>());
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
                    offerTrackBone.remove(4);
            }
        }

        ArrayList<OfferTrackCard> offerTrack = new ArrayList<OfferTrackCard>();
        for(ArrayList<OfferAction> actions: offerTrackBone){
            offerTrack.add(new OfferTrackCard(actions));
        }
        return offerTrack;
    }

    public Integer loadSeed()throws IOException{
        InputStream inputStream = getClass().getResourceAsStream(this.pathConfig);
        if (inputStream == null) throw new IOException("path json error");
        JsonNode seedNode = mapper.readTree(inputStream).path("board").path("seed");
        return  seedNode.asInt(1234);
    }



}
