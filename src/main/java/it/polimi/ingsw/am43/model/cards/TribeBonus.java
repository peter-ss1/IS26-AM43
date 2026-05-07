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


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classEvent")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TribeBonus.FoodOnSet.class, name = "foodOnSet"),
        @JsonSubTypes.Type(value = TribeBonus.SustenanceDiscountByCharacterType.class, name = "sustenanceDiscount"),
        @JsonSubTypes.Type(value = TribeBonus.FoodOnInventorSymbolPair.class, name = "foodOnInventor"),
        @JsonSubTypes.Type(value = TribeBonus.BonusShamanStars.class, name = "shamanStars")
})

public interface TribeBonus extends Serializable {
    int calculateBonus(Player player);

    void giveBonus(GameObserver observer, Player player, int bonus);

    public static class FoodOnSet implements TribeBonus {
        @Override
        public int calculateBonus(Player player) {
            return player.getTribe().getNumberOfSets() * 5;
        }

        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {
            player.alterFood(bonus);
            if (bonus != 0) {
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), bonus, "food"));
            }
        }
    }

    public static class SustenanceDiscountByCharacterType implements TribeBonus {
        private final CharacterType character;

        @JsonCreator
        public SustenanceDiscountByCharacterType(@JsonProperty("character") CharacterType character) {
            this.character = character;
        }

        @Override
        public int calculateBonus(Player player) {
            if (player.getTribe().getNumberByCharacterType(CharacterType.GATHERER)*3 == player.getSustenanceDiscount()) {
                player.alterSustenanceDiscount(player.getTribe().getNumberByCharacterType(character));
            }
            return player.getTribe().getNumberByCharacterType(character);
        }

        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {
            player.alterSustenanceDiscount(bonus);
            if (bonus != 0) {
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), bonus, "sustenance discount"));
            }
        }
    }

    public static class FoodOnInventorSymbolPair implements TribeBonus {
        @Override
        public int calculateBonus(Player player) {
            return player.getTribe().getInventorSymbolPairs() * 3;
        }

        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {
            player.alterFood(bonus);
            if (bonus != 0) {
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), bonus, "food"));
            }
        }
    }

    public static class BonusShamanStars implements TribeBonus {
        @Override
        public int calculateBonus(Player player) {
            return 3;
        }

        @Override
        public void giveBonus(GameObserver observer, Player player, int bonus) {
            player.alterShamanStars(bonus);
            if (bonus != 0) {
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), bonus, "shaman stars"));
            }
        }
    }
}
