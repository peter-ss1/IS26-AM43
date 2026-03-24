package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.player.Player;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Effects.NoLossInRitualEvent.class, name = "noLossRitual"),
        @JsonSubTypes.Type(value = Effects.DoubleWinInRitualEvent.class, name = "doubleWinRitual"),
        @JsonSubTypes.Type(value = Effects.BonusHuntEvent.class, name = "bonusHunt"),
        @JsonSubTypes.Type(value = Effects.BonusPaintingEvent.class, name = "bonusPainting")
})

@FunctionalInterface
public interface EventEffect<T extends Event> {
    void manifest(Player player, T event);
}
