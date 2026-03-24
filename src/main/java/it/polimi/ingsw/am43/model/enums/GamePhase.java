package it.polimi.ingsw.am43.model.enums;

import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.utils.GameLoader;

import java.io.IOException;

public enum GamePhase {
    PREPARATION {
        @Override
        public void resolvePhase(Game game, Board board) throws IOException {
            GameLoader loader = new GameLoader("config.json");
            board.initBoard(
                    game.getPlayers(),
                    game.getNumPlayers(),
                    loader.loadSeed(),
                    loader.loadFoodModifiers(game.getNumPlayers()),
                    loader.loadTribeDeck(),
                    loader.loadBuildingDeck(),
                    loader.loadOfferTrackCard(game.getNumPlayers())
            );

        }

        ;
    },
    OFFER_TRACK_SELECTION,
    ACTION_RESOLUTION,
    ROUND_ENDING,
    DRAW_FROM_TOP_BONUS_ACTION,
    FINAL_COUNT;

    public void resolvePhase(Game game, Board board) throws IOException {
    }

    ;
}
