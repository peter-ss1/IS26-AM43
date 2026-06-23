package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.Serializable;


/**
 * Strategy describing the recurring bonus granted by a {@link TribeBuilding}.
 * A bonus is split into computing its current value and applying the delta to
 * the player.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classEvent")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TribeBonus.FoodOnSet.class, name = "foodOnSet"),
        @JsonSubTypes.Type(value = TribeBonus.SustenanceDiscountByCharacterType.class, name = "sustenanceDiscount"),
        @JsonSubTypes.Type(value = TribeBonus.FoodOnInventorSymbolPair.class, name = "foodOnInventor"),
        @JsonSubTypes.Type(value = TribeBonus.BonusShamanStars.class, name = "shamanStars")
})

public interface TribeBonus extends Serializable {
    /**
     * Computes the current value of this bonus for the given player.
     *
     * @param player the player the bonus is computed for
     * @return the current bonus value
     */
    int calculateBonus(Player player);

    /**
     * Applies the given bonus amount to the player and notifies the observer.
     *
     * @param observer the observer to notify
     * @param player   the player receiving the bonus
     * @param bonus    the amount to apply
     */
    void giveBonus(GameObserver observer, Player player, int bonus);

    /** Grants food equal to 5 times the number of complete character sets. */
    public static class FoodOnSet implements TribeBonus {
        /** {@inheritDoc} */
        @Override
        public int calculateBonus(Player player) {
            return player.getTribe().getNumberOfSets() * 5;
        }

        /** {@inheritDoc} */
        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {
            player.alterFood(bonus);
            if (bonus != 0) {
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), bonus, "food"));
            }
        }
    }

    /** Grants a sustenance discount based on the number of a given character type. */
    public static class SustenanceDiscountByCharacterType implements TribeBonus {
        private final CharacterType character;

        /**
         * @param character the character type the discount is based on
         */
        @JsonCreator
        public SustenanceDiscountByCharacterType(@JsonProperty("character") CharacterType character) {
            this.character = character;
        }

        /** {@inheritDoc} */
        @Override
        public int calculateBonus(Player player) {
            if (player.getTribe().getNumberByCharacterType(CharacterType.GATHERER)*3 == player.getSustenanceDiscount()) {
                player.alterSustenanceDiscount(player.getTribe().getNumberByCharacterType(character));
            }
            return player.getTribe().getNumberByCharacterType(character);
        }

        /** {@inheritDoc} */
        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {
            player.alterSustenanceDiscount(bonus);
            if (bonus != 0) {
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), bonus, "sustenance discount"));
            }
        }
    }

    /** Grants food equal to 3 times the number of inventor symbol pairs. */
    public static class FoodOnInventorSymbolPair implements TribeBonus {
        /** {@inheritDoc} */
        @Override
        public int calculateBonus(Player player) {
            return player.getTribe().getInventorSymbolPairs() * 3;
        }

        /** {@inheritDoc} */
        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {
            player.alterFood(bonus);
            if (bonus != 0) {
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), bonus, "food"));
            }
        }
    }

    /** Grants 3 shaman stars, but only from the second time it is evaluated onwards. */
    public static class BonusShamanStars implements TribeBonus {
        private boolean first = true;
        /** {@inheritDoc} */
        @Override
        public int calculateBonus(Player player) {
            int result = first ? 0 : 3;
            first = false;
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {
            player.alterShamanStars(bonus);
            if (bonus != 0) {
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), bonus, "shaman stars"));
            }
        }
    }
}
