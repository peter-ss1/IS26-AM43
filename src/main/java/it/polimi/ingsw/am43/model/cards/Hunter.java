package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

/**
 * Character card that can grant food when it enters a player's tribe.
 */
public class Hunter extends CharacterCard {
    private final boolean active;

    /**
     * Creates a {@code Hunter} card with the specified era, identifier, and active flag.
     *
     * @param era the era associated with the card
     * @param id the identifier of the card
     * @param active whether this hunter grants its entrance food effect
     */
    public Hunter(int era, int id, boolean active) {
        super(era, id);
        this.active = active;
    }

    /**
     * Returns whether this hunter grants its entrance food effect.
     *
     * @return {@code true} if this hunter is active, otherwise {@code false}
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Adds this hunter to the player's tribe, applies the active hunter effect, and activates tribe buildings.
     *
     * @param observer the game observer used to broadcast the hunter effect and activate tribe buildings
     * @param player the player whose tribe receives this card
     */
    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        if (active) {
            int food = player.getTribe().getNumberByCharacterType(CharacterType.HUNTER);
            player.alterFood(food);
            observer.broadcast(new Update.HunterEffectUpdate(player.getNickname(), food));
        }
        player.getTribe().activateTribeBuildings(observer, player);
    }
}
