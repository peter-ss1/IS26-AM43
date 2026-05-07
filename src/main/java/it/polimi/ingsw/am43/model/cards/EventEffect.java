package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classEvent")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EventEffect.NoLossInRitualEvent.class, name = "noLossRitual"),
        @JsonSubTypes.Type(value = EventEffect.DoubleWinInRitualEvent.class, name = "doubleWinRitual"),
        @JsonSubTypes.Type(value = EventEffect.BonusHuntEvent.class, name = "bonusHunt"),
        @JsonSubTypes.Type(value = EventEffect.BonusPaintingEvent.class, name = "bonusPainting")
})

@FunctionalInterface
public interface EventEffect<T extends Event> extends Serializable {
    void manifest(GameObserver observer, Player player, T event);

    public static class NoLossInRitualEvent implements EventEffect<RitualEvent> {

        @Override
        public void manifest(GameObserver observer, Player player, RitualEvent event) {
            if(event.isLoser(player)) {
                player.alterPrestigePoints(-event.getMalus());
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), -event.getMalus(), "prestige points"));
            }
        }
    }

    public static class DoubleWinInRitualEvent implements EventEffect<RitualEvent> {

        @Override
        public void manifest(GameObserver observer, Player player, RitualEvent event) {
            if(event.isWinner(player)) {
                player.alterPrestigePoints(event.getEra()*5);
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), event.getEra()*5, "prestige points"));
            }
        }
    }

    public static class BonusHuntEvent implements EventEffect<HuntEvent> {

        @Override
        public void manifest(GameObserver observer, Player player, HuntEvent event) {
            int numHunters = player.getTribe().getNumberByCharacterType(CharacterType.HUNTER);
            player.alterPrestigePoints(numHunters);
            player.alterFood(numHunters);
            if (numHunters != 0) observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), numHunters, "prestige points & food"));
        }
    }

    public static class BonusPaintingEvent implements EventEffect<PaintingEvent> {

        @Override
        public void manifest(GameObserver observer, Player player, PaintingEvent event) {
            int numArtists = player.getTribe().getNumberByCharacterType(CharacterType.ARTIST);
            player.alterFood(numArtists);
            if (numArtists != 0) observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), numArtists, "food"));
        }
    }
}
