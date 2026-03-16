package it.polimi.ingsw.am43.model.player;

import it.polimi.ingsw.am43.model.enums.Color;

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
        this.nickname=nickname;
        this.food=0;
        this.prestigePoints=0;
        this.sustenanceDiscount=0;
        this.buildingDiscount=0;
        this.shamanStars=0;
        this.tribe=new Tribe();
    }

    public String getNickname() {
        return nickname;
    }

    public Color getColor(){
        return color;
    }
    public void  setColor(Color color) {
        this.color = color;
    }

    public int  getFood() {
        return food;
    }
    public void alterFood(int delta) {
        this.food += delta;
    }

    public int  getPrestigePoints()  {
        return prestigePoints;
    }
    public void alterPrestigePoints(int delta){
        this.prestigePoints += delta;  //AGGIUNGI ONTROLLO
    }

    public int  getSustenanceDiscount(){
        return sustenanceDiscount;
    }
    public void alterSustenanceDiscount(int delta) {
        this.sustenanceDiscount += delta;
    }

    public int  getBuildingDiscount(){
        return buildingDiscount;
    }
    public void alterBuildingDiscount(int delta){
        this.buildingDiscount += delta; }


    public int  getShamanStars(){
        return shamanStars;
    }
    public void alterShamanStars(int delta) {
        this.shamanStars += delta;
    }

    public Tribe getTribe() {
        return tribe;
    }
}