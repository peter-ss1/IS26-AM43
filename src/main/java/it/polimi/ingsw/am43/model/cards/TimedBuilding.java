package it.polimi.ingsw.am43.model.cards;

import it.polimi.ingsw.am43.client.view.TextFormat;
import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;
import it.polimi.ingsw.am43.network.message.Update;

public class TimedBuilding extends Building {
    private final TimedEffect effect;

    public TimedBuilding(int era, int id, int cost, int prestigePoints, TimedEffect effect) {
        super(era, id, cost, prestigePoints);
        this.effect = effect;
    }

    @Override
    public void tribeEntranceEffect(GameObserver observer, Player player) {
        player.getTribe().addCardToTribe(this);
        player.alterFood(-(this.getCost()));
        observer.broadcast(new Update.BuildingBoughtUpdate(player.getNickname(),  this.getCost()));
    }

    public void TimedBuildingEffect(Player player, Game game, Board board) {
        this.effect.manifest(player, game, board);
    }
    @Override
    public String toString() {
        return "Edificio a Tempo (Costo: " + getCost() + " cibo, PV: " + getPrestigePoints() + ") - Fornisce bonus in specifiche fasi del round.";
    }

    @Override
    public String[] getASCII() {
        String[] lines = new String[7];
        lines[0] = "┌─────────────┐";
        lines[1] = "│" + TextFormat.BLUE + TextFormat.BOLD + " EDIF. TEMPO " + TextFormat.RESET + "│";
        lines[2] = "├─────────────┤";
        lines[3] = "│ Era: " + getEra() + "      │";
        lines[4] = "│ Costo: " + getCost() + "    │";
        lines[5] = "│ PP: " + getPrestigePoints() + "       │";
        lines[6] = "└─────────────┘";
        return lines;
    }
}
