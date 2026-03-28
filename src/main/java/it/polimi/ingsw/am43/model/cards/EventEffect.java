package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EventEffect.NoLossInRitualEvent.class, name = "noLossRitual"),
        @JsonSubTypes.Type(value = EventEffect.DoubleWinInRitualEvent.class, name = "doubleWinRitual"),
        @JsonSubTypes.Type(value = EventEffect.BonusHuntEvent.class, name = "bonusHunt"),
        @JsonSubTypes.Type(value = EventEffect.BonusPaintingEvent.class, name = "bonusPainting")
})

@FunctionalInterface
public interface EventEffect<T extends Event> {
    void manifest(Player player, T event);

    public static class NoLossInRitualEvent implements EventEffect<RitualEvent> {

        @Override
        public void manifest(Player player, RitualEvent event) {
            if(event.isLoser(player)) {
                player.alterPrestigePoints(-event.getMalus());
            }
        }
    }

    public static class DoubleWinInRitualEvent implements EventEffect<RitualEvent> {

        @Override
        public void manifest(Player player, RitualEvent event) {
            if(event.isWinner(player)) {
                player.alterPrestigePoints(event.getEra()*5);
            }
        }
    }

    public static class BonusHuntEvent implements EventEffect<HuntEvent> {

        @Override
        public void manifest(Player player, HuntEvent event) {
            player.alterPrestigePoints(player.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
            player.alterFood(player.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
        }
    }

    public static class BonusPaintingEvent implements EventEffect<PaintingEvent> {

        @Override
        public void manifest(Player player, PaintingEvent event) {
            player.alterFood(player.getTribe().getNumberByCharacterType(CharacterType.ARTIST));
        }
    }
}
