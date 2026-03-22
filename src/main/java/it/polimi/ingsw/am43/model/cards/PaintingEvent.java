package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.player.Player;

import java.util.List;

public class PaintingEvent extends Event {

    public PaintingEvent(int era) {
        super(era);
    }

    @Override
    public void affectPlayers(List<Player> players) {
        for (Player p : players) {
            int amount = p.getTribe().getNumberByCharacterType(CharacterType.ARTIST);
            if (amount < this.getEra()) {
                p.alterPrestigePoints(-2);
            }
            else {
                p.alterPrestigePoints(amount*this.getEra());
            }
            p.getTribe().activateEventBuildings(this, p);
        }
    }

    @Override
    public void triggerBuilding(EventBuilding building, Player player) {
        building.reactToEvent(player, this);
    }
}
