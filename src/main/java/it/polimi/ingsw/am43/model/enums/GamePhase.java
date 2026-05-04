package it.polimi.ingsw.am43.model.enums;

import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameLoader;

import java.io.IOException;
import java.util.Comparator;

public enum GamePhase {
    PREPARATION {
        @Override
        public void resolvePhase(Game game, Board board) {
            GameLoader loader = new GameLoader("/it/polimi/ingsw/am43/config.json");
            try {
                game.initBoard(
                        game.getPlayers(),
                        game.getNumPlayers(),
                        loader.loadFoodModifiers(game.getNumPlayers()),
                        loader.loadTribeDeck(game.getNumPlayers()),
                        loader.loadBuildingDeck(game.getNumPlayers()),
                        loader.loadOfferTrackCard(game.getNumPlayers())
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            game.setIdCard(loader.loadIdToCardMap());
            game.setPhase(GamePhase.OFFER_TRACK_SELECTION);
        }
    },
    OFFER_TRACK_SELECTION {
        @Override
        public void resolvePhase(Game game, Board board) {
            game.setPhase(GamePhase.ACTION_RESOLUTION);
            board.getNextPlayerOnOfferTrack().ifPresentOrElse(player -> {
                        game.setCurrPlayer(player);
                        game.resolveOffer(player);
                    },
                    () -> {
                        game.getPhase().resolvePhase(game, board);
                    });
        }
    },
    ACTION_RESOLUTION {
        @Override
        public void resolvePhase(Game game, Board board) {
            game.setPhase(ROUND_ENDING);
            for (Player player : game.getPlayers()) {
                player.getTribe().activateTimedBuilding(game, player, board);
            }
            if (game.getPhase().equals(ROUND_ENDING)) {
                game.getPhase().resolvePhase(game, board);
            }
        }
    },
    ROUND_ENDING {
        @Override
        public void resolvePhase(Game game, Board board) {
            board.activateEvents(OfferAction.BOTTOM, game.getPlayers());
            if (board.checkFinalRound()) {
                board.activateEvents(OfferAction.TOP, game.getPlayers());
                game.setPhase(FINAL_COUNT);
                game.getPhase().resolvePhase(game, board);
                return;
            }
            board.moveTopToBottomTribe();
            board.replenishTopRow(game.getNumPlayers() + 4);
            game.setPhase(OFFER_TRACK_SELECTION);
            game.setCurrPlayer(board.getNextPlayerInOrderQueue());
        }
    },
    DRAW_FROM_TOP_BONUS_ACTION {
        @Override
        public void resolvePhase(Game game, Board board) {
            game.setPhase(ROUND_ENDING);
            game.getPhase().resolvePhase(game, board);
        }
    },

    FINAL_COUNT {
        @Override
        public void resolvePhase(Game game, Board board) {
            game.getPlayers().forEach(Player::countFinalPoints);
            game.getPlayers().sort(Comparator.comparingInt(Player::getPrestigePoints).thenComparing(Player::getFood).reversed());
        }
    };

    public void resolvePhase(Game game, Board board) throws RuntimeException {
    }
}
