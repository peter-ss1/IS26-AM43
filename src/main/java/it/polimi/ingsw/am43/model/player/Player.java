package it.polimi.ingsw.am43.model.player;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.enums.PlayerStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {

    private final String nickname;
    private Color color;
    private int food;
    private int prestigePoints;
    private int sustenanceDiscount;
    private int buildingDiscount;
    private int shamanStars;
    private final Tribe tribe;
    private List<OfferAction> availableActions;
    private PlayerStatus status;

    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
        this.food = 0;
        this.prestigePoints = 0;
        this.sustenanceDiscount = 0;
        this.buildingDiscount = 0;
        this.shamanStars = 0;
        this.tribe = new Tribe();
        this.availableActions = new ArrayList<OfferAction>();
        this.status=PlayerStatus.ACTIVE;
    }



    public String getNickname() {
        return nickname;
    }

    public void setStatus(PlayerStatus status){
        this.status=status;
    }

    public PlayerStatus getStatus(){
        return this.status;
    }




    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }


    public int getFood() {
        return food;
    }


    public void alterFood(int amount) {
        this.food = Math.max(0, this.food + amount);
    }

    public void setAvailableActions(List<OfferAction> offerActions) {
        this.availableActions = offerActions;
    }

    public ArrayList<OfferAction> getAvailableActions() {
        return new ArrayList<>(this.availableActions);
    }

    public void removeAvailableAction(OfferAction offerAction) {
        this.availableActions.remove(offerAction);
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public void alterPrestigePoints(int amount) {
        this.prestigePoints += amount;
    }


    public int getSustenanceDiscount() {
        return sustenanceDiscount;
    }

    public void alterSustenanceDiscount(int amount) {
        this.sustenanceDiscount += amount;
    }


    public int getBuildingDiscount() {
        return buildingDiscount;
    }


    public void alterBuildingDiscount(int amount) {
        this.buildingDiscount += amount;
    }


    public int getShamanStars() {
        return shamanStars;
    }


    public void alterShamanStars(int amount) {
        this.shamanStars += amount;
    }


    public Tribe getTribe() {
        return tribe;
    }

    public void countFinalPoints() {
        alterPrestigePoints(tribe.getBuildersTotalPrestigePoints());
        int inventors = tribe.getNumberByCharacterType(CharacterType.INVENTOR);
        alterPrestigePoints(inventors * tribe.getDistinctInventorSymbols());
        int artists = tribe.getNumberByCharacterType(CharacterType.ARTIST);
        alterPrestigePoints((artists / 2) * 10);
        alterPrestigePoints(tribe.getBuildingsTotalPrestigePoints());
        tribe.activateFinalBuildings(this);
    }


}