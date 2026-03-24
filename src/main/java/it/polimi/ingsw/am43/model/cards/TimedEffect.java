package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.player.Player;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Effects.BonusTurnFood.class, name = "bonusTurnFood"),
        @JsonSubTypes.Type(value = Effects.BonusPickCard.class, name = "bonusPickCard")
})
@FunctionalInterface
public interface TimedEffect {
    void manifest(Player player, Game game);
}
