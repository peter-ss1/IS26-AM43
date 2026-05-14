package it.polimi.ingsw.am43.model.enums;

import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameLoader;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum GamePhase {
    PREPARATION {
        @Override
        public void resolvePhase(Game game, Board board) {
            GameLoader loader = new GameLoader("/it/polimi/ingsw/am43/config.json");
            try {
                game.initBoard(
                        game.getAllPlayers(),
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
                            game.wakeUpWaiting();
                            game.getPhase().resolvePhase(game, board);
                    });
        }
    },
    ACTION_RESOLUTION {
        @Override
        public void resolvePhase(Game game, Board board) {
            game.setPhase(ROUND_ENDING);
            for (Player player : game.getActivePlayers()) {
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
            board.activateEvents(game.getObserver(), OfferAction.BOTTOM, game.getAllPlayers());
            if (board.checkFinalRound()) {
                board.activateEvents(game.getObserver(), OfferAction.TOP, game.getAllPlayers());
                game.setPhase(FINAL_COUNT);
                game.getPhase().resolvePhase(game, board);
                return;
            }
            board.moveTopToBottomTribe();
            board.replenishTopRow(game.getNumPlayers() + 4);
            board.buildNewRoundUpdate(game.getObserver());
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
            List<Player> players= game.getAllPlayers();
            players.forEach(Player::countFinalPoints);
            players.sort(Comparator.comparingInt(Player::getPrestigePoints).thenComparing(Player::getFood).reversed());
            Player winner = players.getFirst();
            List<String> winners = players.stream()
                    .filter(p -> p.getPrestigePoints() == winner.getPrestigePoints() && p.getFood() == winner.getFood())
                    .map(Player::getNickname).toList();
            game.getObserver().broadcast(new Update.GameOverUpdate(winners));
        }
    };

    public void resolvePhase(Game game, Board board) throws RuntimeException {
    }
}
