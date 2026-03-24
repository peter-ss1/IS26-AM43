package it.polimi.ingsw.am43.model.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.player.Player;



public class Effects {
    public static class FoodOnSet implements TribeBonus {
        @Override
        public int calculateBonus(Player player) {
            return player.getTribe().getNumberOfSets() * 5;
        }

        @Override
        public void giveBonus(Player player, int bonus) {
            player.alterFood(bonus);
        }
    }

    public static class SustenanceDiscountByCharacterType implements TribeBonus {
        private final CharacterType character;

        @JsonCreator
        public SustenanceDiscountByCharacterType(@JsonProperty("character") CharacterType character) {
            this.character = character;
        }

        @Override
        public int calculateBonus(Player player) {
            return player.getTribe().getNumberByCharacterType(character);
        }

        @Override
        public void giveBonus(Player player, int bonus) {
            player.alterSustenanceDiscount(bonus);
        }
    }

    public static class FoodOnInventorSymbolPair implements TribeBonus {
        @Override
        public int calculateBonus(Player player) {
            return player.getTribe().getInventorSymbolPairs() * 3;
        }

        @Override
        public void giveBonus(Player player, int bonus) {
            player.alterFood(bonus);
        }
    }

    public static class BonusShamanStars implements TribeBonus {
        @Override
        public int calculateBonus(Player player) {
            return 3;
        }

        @Override
        public void giveBonus(Player player, int bonus) {
            player.alterShamanStars(bonus);
        }
    }

    public static class FinalPrestigePointsByCharacterType implements FinalEffect {
        private final CharacterType character;
        private final int amount;

        @JsonCreator
        public FinalPrestigePointsByCharacterType(@JsonProperty("character") CharacterType character,@JsonProperty("amount") int amount) {
            this.character = character;
            this.amount = amount;
        }

        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(player.getTribe().getNumberByCharacterType(this.character) * this.amount);
        }
    }

    public static class FinalDoubleBuilderPrestigePoints implements FinalEffect {
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(player.alterPrestigePoints(player.getTribe().getBuildersPrestigePoints());
        }
    }

    public static class FinalPrestigePointsPerSet implements FinalEffect {
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(player.getTribe().getNumberOfSets() * 6);
        }
    }

    public static class FinalBonusPrestigePoints implements FinalEffect {
        @Override
        public void manifest(Player player) {
            player.alterPrestigePoints(25);
        }
    }

    public static class BonusTurnFood implements TimedEffect {

        @Override
        public void manifest(Player player, Game game) {
            if (game.getPhase() == GamePhase.ACTION_RESOLUTION && player == game.getCurrPlayer() /*&& game.isTurnOrderNotFull*/) {
                player.alterFood(1);
            }
        }
    }

    public static class BonusPickCard implements TimedEffect {

        @Override
        public void manifest(Player player, Game game) {
            if (game.getPhase() == GamePhase.ACTION_RESOLUTION /*&& game.isTurnOrderFull*/) {
                game.setPhase(GamePhase.DRAW_FROM_TOP_BONUS_ACTION);
                game.setCurrPlayer(player);
                game.getPhase().resolvePhase();
            }
        }
    }

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

    public static class BonusPaintingEvent implements EventEffect<HuntEvent> {

        @Override
        public void manifest(Player player, HuntEvent event) {
            player.alterFood(player.getTribe().getNumberByCharacterType(CharacterType.ARTIST));
        }
    }
}
