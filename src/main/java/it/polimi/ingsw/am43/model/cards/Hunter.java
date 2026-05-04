package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

public class Hunter extends CharacterCard {
    private final boolean active;

    public Hunter(int era, int id, boolean active) {
        super(era, id);
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

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