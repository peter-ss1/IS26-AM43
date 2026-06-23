package it.polimi.ingsw.am43.model.player;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.OfferAction;
import it.polimi.ingsw.am43.model.enums.PlayerStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a participant in the match, tracking individual resources
 * such as food and prestige points, persistent bonuses, and offer-related actions.
 * It also tracks the connection status of the player.
 */
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

    /**
     * Constructs a new Player entity with standard properties.
     *
     * @param nickname the unique identification name chosen by the player
     * @param color    the unique totem color chosen by the player
     */
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



    /** @return the player's nickname */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the player's status (ACTIVE, INACTIVE, WAITING).
     *
     * @param status the new status
     */
    public void setStatus(PlayerStatus status){
        this.status=status;
    }

    /** @return the player's current status */
    public PlayerStatus getStatus(){
        return this.status;
    }




    /** @return the player's color */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the player's new color
     */
    public void setColor(Color color) {
        this.color = color;
    }


    /** @return the current food points */
    public int getFood() {
        return food;
    }


    /**
     * Changes the player's food. The result can never drop below zero.
     *
     * @param amount the variation (positive = gain, negative = spend)
     */
    public void alterFood(int amount) {
        this.food = Math.max(0, this.food + amount);
    }

    /**
     * Sets the actions available for the current turn.
     * An empty list means the player can no longer pick cards.
     *
     * @param offerActions the list of rows the player can pick from
     */
    public void setAvailableActions(List<OfferAction> offerActions) {
        this.availableActions = offerActions;
    }

    /** @return a copy of the actions available for the current turn */
    public ArrayList<OfferAction> getAvailableActions() {
        return new ArrayList<>(this.availableActions);
    }

    /**
     * Removes an action from the available-actions list after it has been used.
     *
     * @param offerAction the action to remove
     */
    public void removeAvailableAction(OfferAction offerAction) {
        this.availableActions.remove(offerAction);
    }

    /** @return the accumulated prestige points */
    public int getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * Changes the player's prestige points.
     *
     * @param amount the variation (positive = gain)
     */
    public void alterPrestigePoints(int amount) {
        this.prestigePoints += amount;
    }


    /** @return the current discount on the sustenance cost */
    public int getSustenanceDiscount() {
        return sustenanceDiscount;
    }

    /**
     * @param amount the variation of the sustenance discount
     */
    public void alterSustenanceDiscount(int amount) {
        this.sustenanceDiscount += amount;
    }


    /** @return the current discount on the building cost */
    public int getBuildingDiscount() {
        return buildingDiscount;
    }


    /**
     * @param amount the variation of the building discount
     */
    public void alterBuildingDiscount(int amount) {
        this.buildingDiscount += amount;
    }


    /** @return the number of accumulated shaman stars */
    public int getShamanStars() {
        return shamanStars;
    }


    /**
     * @param amount the variation of the shaman stars
     */
    public void alterShamanStars(int amount) {
        this.shamanStars += amount;
    }


    /** @return the player's tribe (collection of acquired cards) */
    public Tribe getTribe() {
        return tribe;
    }

    /**
     * Computes and adds the player's final points:
     * Builder (fixed points), Inventor (multiplicative: n × distinct symbols),
     * Artist (each pair is worth 10 points), buildings and FinalBuilding.
     */
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