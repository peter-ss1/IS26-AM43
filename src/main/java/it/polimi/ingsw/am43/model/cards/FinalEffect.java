package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;

import java.io.Serializable;
/**
 * Strategy describing the end-game scoring effect of a {@link FinalBuilding}.
 * Each concrete implementation encodes one way of granting prestige points to
 * a player when the game ends.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classEvent")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FinalEffect.FinalPrestigePointsByCharacterType.class, name = "finalPointsByType"),
        @JsonSubTypes.Type(value = FinalEffect.FinalDoubleBuilderPrestigePoints.class, name = "finalDoubleBuilder"),
        @JsonSubTypes.Type(value = FinalEffect.FinalPrestigePointsPerSet.class, name = "finalPointsPerSet"),
        @JsonSubTypes.Type(value = FinalEffect.FinalBonusPrestigePoints.class, name = "finalBonusPoints")
})


@FunctionalInterface
public interface FinalEffect extends Serializable {
    /**
     * Applies this end-game effect to the given player.
     *
     * @param player the player the effect is applied to
     */
    void manifest(Player player);

    /**
     * Grants prestige points proportional to how many characters of a given
     * type the player owns.
     */
    public static class FinalPrestigePointsByCharacterType implements FinalEffect {
        private final CharacterType character;
        private final int amount;

        /**
         * @param character the character type counted in the player's tribe
         * @param amount    the prestige points granted per matching character
         */
        @JsonCreator
        public FinalPrestigePointsByCharacterType(@JsonProperty("character") CharacterType character, @JsonProperty("amount") int amount) {
            this.character = character;
            this.amount = amount;
        }

        /** {@inheritDoc} */
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(player.getTribe().getNumberByCharacterType(this.character) * this.amount);
        }
    }

    /** Grants prestige points equal to the total prestige points of the player's builders. */
    public static class FinalDoubleBuilderPrestigePoints implements FinalEffect {
        /** {@inheritDoc} */
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(player.getTribe().getBuildersTotalPrestigePoints());
        }
    }

    /** Grants 6 prestige points for each complete set of characters the player owns. */
    public static class FinalPrestigePointsPerSet implements FinalEffect {
        /** {@inheritDoc} */
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(player.getTribe().getNumberOfSets() * 6);
        }
    }

    /** Grants a flat bonus of 25 prestige points. */
    public static class FinalBonusPrestigePoints implements FinalEffect {
        /** {@inheritDoc} */
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(25);
        }
    }
}
