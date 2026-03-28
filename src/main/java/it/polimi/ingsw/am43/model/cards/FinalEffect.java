package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FinalEffect.FinalPrestigePointsByCharacterType.class, name = "finalPointsByType"),
        @JsonSubTypes.Type(value = FinalEffect.FinalDoubleBuilderPrestigePoints.class, name = "finalDoubleBuilder"),
        @JsonSubTypes.Type(value = FinalEffect.FinalPrestigePointsPerSet.class, name = "finalPointsPerSet"),
        @JsonSubTypes.Type(value = FinalEffect.FinalBonusPrestigePoints.class, name = "finalBonusPoints")
})

@FunctionalInterface
public interface FinalEffect {
    void manifest(Player player);

    public static class FinalPrestigePointsByCharacterType implements FinalEffect {
        private final CharacterType character;
        private final int amount;

        @JsonCreator
        public FinalPrestigePointsByCharacterType(@JsonProperty("character") CharacterType character, @JsonProperty("amount") int amount) {
            this.character = character;
            this.amount = amount;
        }

        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(player.getTribe().getNumberByCharacterType(this.character) * this.amount);
        }
    }

    public static class FinalDoubleBuilderPrestigePoints implements FinalEffect {
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(player.getTribe().getBuildersTotalPrestigePoints());
        }
    }

    public static class FinalPrestigePointsPerSet implements FinalEffect {
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(player.getTribe().getNumberOfSets() * 6);
        }
    }

    public static class FinalBonusPrestigePoints implements FinalEffect {
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(25);
        }
    }
}
