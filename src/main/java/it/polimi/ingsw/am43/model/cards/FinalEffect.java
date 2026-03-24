package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.player.Player;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Effects.FinalPrestigePointsByCharacterType.class, name = "finalPointsByType"),
        @JsonSubTypes.Type(value = Effects.FinalDoubleBuilderPrestigePoints.class, name = "finalDoubleBuilder"),
        @JsonSubTypes.Type(value = Effects.FinalPrestigePointsPerSet.class, name = "finalPointsPerSet"),
        @JsonSubTypes.Type(value = Effects.FinalBonusPrestigePoints.class, name = "finalBonusPoints")
})

@FunctionalInterface
public interface FinalEffect {
    void manifest(Player player);
}
