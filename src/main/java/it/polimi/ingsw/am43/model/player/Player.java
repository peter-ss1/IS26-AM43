package it.polimi.ingsw.am43.model.player;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Tribe;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.enums.CharacterType;

public class Player {

    private String nickname;
    private Color color;
    private int food;
    private int prestigePoints;
    private int sustenanceDiscount;
    private int buildingDiscount;
    private int shamanStars;
    private Tribe tribe;

    public Player(String nickname) {
        this.nickname = nickname;
        this.food = 0;
        this.prestigePoints = 0;
        this.sustenanceDiscount = 0;
        this.buildingDiscount = 0;
        this.shamanStars = 0;
        this.tribe = new Tribe();
    }


    public String getNickname() {
        return nickname;
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
        this.food += amount;
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