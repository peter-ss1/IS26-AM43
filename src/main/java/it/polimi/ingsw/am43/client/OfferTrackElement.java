package it.polimi.ingsw.am43.client;

import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.OfferAction;

import java.io.Serializable;
import java.util.List;

public record OfferTrackElement(List<OfferAction> offerActions, Color color) implements Serializable {
    public String[] prepareCard() {
        return new String[] {
                "┌───────────┐",
                "│  OFFERTA  │",
                "├───────────┤",
                "│           │",
                "│ " + color + " │",
                "│           │",
                "├───────────┤",
                "│ " + offerActions + " │",
                "└───────────┘"
        };
    }
}
