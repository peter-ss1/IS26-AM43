package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.player.Player;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Effects.FoodOnSet.class, name = "foodOnSet"),
        @JsonSubTypes.Type(value = Effects.SustenanceDiscountByCharacterType.class, name = "sustenanceDiscount"),
        @JsonSubTypes.Type(value = Effects.FoodOnInventorSymbolPair.class, name = "foodOnInventor"),
        @JsonSubTypes.Type(value = Effects.BonusShamanStars.class, name = "shamanStars")
})

public interface TribeBonus {
    int calculateBonus(Player player);

    void giveBonus(Player player, int bonus);
}
