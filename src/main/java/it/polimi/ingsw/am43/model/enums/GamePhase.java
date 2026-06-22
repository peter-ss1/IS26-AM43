package it.polimi.ingsw.am43.model.enums;

import it.polimi.ingsw.am43.model.board.Board;
import it.polimi.ingsw.am43.model.board.Game;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameLoader;
import it.polimi.ingsw.am43.network.message.Update;

import java.io.IOException;
import java.util.Map;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The phases of the game state machine. Each constant overrides
 * {@link #resolvePhase(Game, Board)} to perform the logic of that phase and to
 * transition the game to the next one.
 */
public enum GamePhase {
    /** Initial setup: loads the decks and the board, then moves on to the offer track selection. */
    PREPARATION {
        /** {@inheritDoc} */
        @Override
        public void resolvePhase(Game game, Board board) {
            GameLoader loader = new GameLoader(game.getSeed());
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
    /** Picks the next player on the offer track and resolves their offer. */
    OFFER_TRACK_SELECTION {
        /** {@inheritDoc} */
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
    /** Resolves the current player's action and activates timed buildings. */
    ACTION_RESOLUTION {
        /** {@inheritDoc} */
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
    /** Resolves end-of-round events and either starts the final count or sets up a new round. */
    ROUND_ENDING {
        /** {@inheritDoc} */
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
    /** Handles the bonus top-row draw granted by a timed building, then ends the round. */
    DRAW_FROM_TOP_BONUS_ACTION {
        /** {@inheritDoc} */
        @Override
        public void resolvePhase(Game game, Board board) {
            game.setPhase(ROUND_ENDING);
            game.getPhase().resolvePhase(game, board);
        }
    },

    /** Computes final scores, determines the winner(s) and ends the game. */
    FINAL_COUNT {
        /** {@inheritDoc} */
        @Override
        public void resolvePhase(Game game, Board board) {
            List<Player> players= game.getAllPlayers().stream().filter(p -> p.getStatus() != PlayerStatus.INACTIVE).collect(Collectors.toList());
            players.forEach(Player::countFinalPoints);
            players.sort(Comparator.comparingInt(Player::getPrestigePoints).thenComparing(Player::getFood).reversed());
            Player winner = players.getFirst();
            List<String> winners = players.stream()
                    .filter(p -> p.getPrestigePoints() == winner.getPrestigePoints() && p.getFood() == winner.getFood())
                    .map(Player::getNickname).toList();
            game.getObserver().broadcast(new Update.GameOverUpdate(winners, players.stream().collect(Collectors.toMap(Player::getNickname, Player::getPrestigePoints))));
            game.getObserver().notifyEndGame();
        }
    };

    /**
     * Executes the logic of this phase and transitions the game to the next one.
     * The default implementation does nothing; each phase overrides it.
     *
     * @param game  the current game
     * @param board the current board
     * @throws RuntimeException if the phase logic fails (e.g. during loading)
     */
    public void resolvePhase(Game game, Board board) throws RuntimeException {
    }
}
