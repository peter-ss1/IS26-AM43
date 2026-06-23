package it.polimi.ingsw.am43.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.Serializable;
/**
 * Strategy describing how an event building reacts to a given event type.
 * Each concrete implementation encodes one event-building effect; the type
 * parameter binds it to the specific {@link Event} subtype it handles.
 *
 * @param <T> the event type this effect reacts to
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classEvent")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EventEffect.NoLossInRitualEvent.class, name = "noLossRitual"),
        @JsonSubTypes.Type(value = EventEffect.DoubleWinInRitualEvent.class, name = "doubleWinRitual"),
        @JsonSubTypes.Type(value = EventEffect.BonusHuntEvent.class, name = "bonusHunt"),
        @JsonSubTypes.Type(value = EventEffect.BonusPaintingEvent.class, name = "bonusPainting")
})


@FunctionalInterface
public interface EventEffect<T extends Event> extends Serializable {
    /**
     * Applies this effect to the player when the bound event is resolved.
     *
     * @param observer the observer to notify
     * @param player   the owner of the building
     * @param event    the event being resolved
     */
    void manifest(GameObserver observer, Player player, T event);

    /** Effect that cancels the ritual malus for a losing player. */
    public static class NoLossInRitualEvent implements EventEffect<RitualEvent> {

        /** {@inheritDoc} */
        @Override
        public void manifest(GameObserver observer, Player player, RitualEvent event) {
            if(event.isLoser(player)) {
                player.alterPrestigePoints(-event.getMalus());
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), -event.getMalus(), "prestige points"));
            }
        }
    }

    /** Effect that doubles the ritual reward (era × 5 prestige points) for a winning player. */
    public static class DoubleWinInRitualEvent implements EventEffect<RitualEvent> {

        /** {@inheritDoc} */
        @Override
        public void manifest(GameObserver observer, Player player, RitualEvent event) {
            if(event.isWinner(player)) {
                player.alterPrestigePoints(event.getEra()*5);
                observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), event.getEra()*5, "prestige points"));
            }
        }
    }

    /** Effect that grants prestige points and food equal to the player's number of hunters. */
    public static class BonusHuntEvent implements EventEffect<HuntEvent> {

        /** {@inheritDoc} */
        @Override
        public void manifest(GameObserver observer, Player player, HuntEvent event) {
            int numHunters = player.getTribe().getNumberByCharacterType(CharacterType.HUNTER);
            player.alterPrestigePoints(numHunters);
            player.alterFood(numHunters);
            if (numHunters != 0) observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), numHunters, "prestige points & food"));
        }
    }

    /** Effect that grants food equal to the player's number of artists. */
    public static class BonusPaintingEvent implements EventEffect<PaintingEvent> {

        /** {@inheritDoc} */
        @Override
        public void manifest(GameObserver observer, Player player, PaintingEvent event) {
            int numArtists = player.getTribe().getNumberByCharacterType(CharacterType.ARTIST);
            player.alterFood(numArtists);
            if (numArtists != 0) observer.broadcast(new Update.BuildingEffectUpdate(player.getNickname(), numArtists, "food"));
        }
    }
}
