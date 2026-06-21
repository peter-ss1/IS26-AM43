package it.polimi.ingsw.am43.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.OfferAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents simplified offer track card data.
 * Tracks the available actions it offers and the color of the player using it, if present.
 */
public class OfferTrackElement implements Serializable {
    @JsonProperty("offerActions")
    private final List<OfferAction> offerActions;
    @JsonProperty("color")
    private Color color;

    public OfferTrackElement(@JsonProperty("offerActions") List<OfferAction> offerActions,@JsonProperty("color") Color color) {
        this.offerActions = offerActions;
        this.color = color;
    }

    public List<OfferAction> getOfferActions() {
        return new ArrayList<>(offerActions);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
}
