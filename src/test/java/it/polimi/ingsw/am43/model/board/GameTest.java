package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.MockObserver;
import it.polimi.ingsw.am43.model.cards.FinalBuilding;
import it.polimi.ingsw.am43.model.cards.FinalEffect;
import it.polimi.ingsw.am43.model.cards.TimedBuilding;
import it.polimi.ingsw.am43.model.cards.TimedEffect;
import it.polimi.ingsw.am43.model.enums.*;
import it.polimi.ingsw.am43.model.exceptions.IllegalMoveException;
import it.polimi.ingsw.am43.model.exceptions.IllegalPlayerInitializationException;
import it.polimi.ingsw.am43.model.exceptions.OutOfTurnException;
import it.polimi.ingsw.am43.model.player.Player;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GameTest {
    private Game game = new Game(2, "player", Color.BLACK);
    private Player player1;
    private Player player2;

    /**
     * Test 1: Game Initialization and Lobby Management.
     * Verifies the initialization phase of the game and the setup of the players.
     * It tests:
     * - That the game initializes in the PREPARATION phase.
     * - Rejection of invalid player join requests (such as duplicate names or choosing an unavailable color).
     * - The player removal mechanism and the corresponding release of their chosen color.
     * - The game start trigger and its transition to the OFFER_TRACK_SELECTION phase.
     * - The verification of initial state values, ensuring all players are generated, a current active
     *   player is assigned, initial resources (food) are distributed, and edge cases like out-of-bounds
     *   card retrieval throw appropriate exceptions.
     */
    @Test
    @Order(1)
    void gameInitializationTest() {
        game = new Game(2, "pippo", Color.BLACK, 19853L);
        game.setObserver(new MockObserver());
        assertEquals(GamePhase.PREPARATION, game.getPhase());
        assertThrows(IllegalPlayerInitializationException.class, () -> game.addPlayer("pippo", Color.WHITE));
        assertThrows(IllegalPlayerInitializationException.class, () -> game.addPlayer("jonny", Color.BLACK));
        game.removePlayer("pippo");
        assertTrue(game.getAvailableColors().contains(Color.BLACK));
        game.addPlayer("pippo", Color.BLACK);
        game.addPlayer("jonny", Color.RED);
        assertFalse(game.getAvailableColors().contains(Color.RED) || game.getAvailableColors().contains(Color.BLACK));
        assertEquals(GamePhase.PREPARATION, game.getPhase());
        game.startGame();
        assertEquals(GamePhase.OFFER_TRACK_SELECTION, game.getPhase());
        assertEquals(2, game.getAllPlayers().size());
        List<String> namePlayers = new ArrayList<>();
        namePlayers.add("pippo");
        namePlayers.add("jonny");
        for (Player player : game.getAllPlayers()) {
            assertTrue(namePlayers.contains(player.getNickname()));
        }
        assertNotNull(game.getCurrPlayer());
        assertTrue(namePlayers.contains(game.getCurrPlayer().getNickname()));
        assertEquals(2, game.getCurrPlayer().getFood());
        assertThrows(IllegalArgumentException.class, () -> game.getCardById(200));
    }

    /**
     * Test 2: Offer Track Selection and Action Assignment.
     * Verifies the mechanics of the offer track selection phase where turn orders and available actions are determined.
     * It tests:
     * - Validations blocking unknown or invalid players from performing actions.
     * - Phase rules preventing players from drawing cards or skipping turns before the totem is placed.
     * - The turn order updates after totem placement.
     * - Automatic phase progression into ACTION_RESOLUTION once all players have placed their totem.
     * - Correct assignment of the available action list for each player depending on their offer track choice.
     */
    @Test
    @Order(2)
    void offerTrackSelectionPhaseTest() throws RuntimeException {
        assertThrows(IllegalArgumentException.class, () -> game.placeTotemOnTrack(new Player("error", Color.WHITE), 1));
        assertThrows(IllegalArgumentException.class, () -> game.endCurrentTurn(new Player("error", Color.WHITE)));
        this.player1 = this.game.getCurrPlayer();
        assertThrows(IllegalStateException.class, () -> game.pickCard(game.getCardById(6), player1));
        assertThrows(IllegalStateException.class, () -> game.endCurrentTurn(player1));
        this.game.placeTotemOnTrack(player1, 3);
        assertThrows(OutOfTurnException.class, () -> game.placeTotemOnTrack(player1, 3));
        assertNotEquals(player1, this.game.getCurrPlayer());
        this.player2 = this.game.getCurrPlayer();
        assertEquals(player2, game.getPlayerByName(game.getCurrPlayer().getNickname()));
        this.game.placeTotemOnTrack(player2, 2);
        assertEquals(GamePhase.ACTION_RESOLUTION, this.game.getPhase());
        assertEquals(player2, this.game.getCurrPlayer());
        ArrayList<OfferAction> actionsExpd1 = new ArrayList<>();
        ArrayList<OfferAction> actionsExpd2 = new ArrayList<>();
        actionsExpd1.add(OfferAction.TOP);
        actionsExpd1.add(OfferAction.TOP);
        actionsExpd2.add(OfferAction.BOTTOM);
        actionsExpd2.add(OfferAction.TOP);
        assertEquals(actionsExpd1, player1.getAvailableActions());
        assertEquals(actionsExpd2, player2.getAvailableActions());
        assertThrows(IllegalArgumentException.class, () -> game.setCurrPlayer(new Player("pippo", Color.WHITE)));
    }

    /**
     * Test 3: Action Resolution and Card Picking.
     * Verifies card-picking rules, tribe and turn updates.
     * It tests:
     * - Rejection of picking requests from invalid players, or selections of unavailable cards.
     * - Proper consumption of action trackers from a player's profile when a card is successfully picked.
     * - Accurate modifications of player and tribe data, validating modifications to building discounts,
     *   character tracking counts, and food resources based on card properties.
     * - Interactivity with passive building effects (such as immediate turn food bonuses).
     * - Automatic transfer of active control to the next player once the current player exhausts their available actions.
     */
    @Test
    @Order(3)
    void actionResolutionPhaseTest() throws RuntimeException {
        assertThrows(IllegalArgumentException.class, () -> game.pickCard(game.getCardById(6), new Player("error", Color.WHITE)));
        assertThrows(IllegalArgumentException.class, () -> game.addPlayer("pippo", Color.WHITE));
        assertThrows(IllegalStateException.class, () -> game.placeTotemOnTrack(player1, 3));
        this.game.pickCard(game.getCardById(6), player2);
        assertEquals(player2, game.getCurrPlayer());
        assertEquals(1, player2.getAvailableActions().size());
        assertEquals(1, player2.getBuildingDiscount());
        assertEquals(1, player2.getTribe().getNumberByCharacterType(CharacterType.BUILDER));
        assertEquals(3, player2.getFood());
        assertThrows(IllegalMoveException.class, () -> this.game.pickCard(game.getCardById(97), player2));
        assertThrows(OutOfTurnException.class, () -> this.game.pickCard(game.getCardById(14), player1));
        player2.getTribe().addCardToTribe(new TimedBuilding(1, 1, 1, 1, new TimedEffect.BonusTurnFood()));
        this.game.pickCard(game.getCardById(14), player2);
        assertEquals(1, player2.getTribe().getNumberByCharacterType(CharacterType.ARTIST));
        assertEquals(5, player2.getFood());
        assertEquals(0, player2.getAvailableActions().size());
        assertEquals(player1, game.getCurrPlayer());
        assertThrows(IllegalArgumentException.class, () -> this.game.pickCard(game.getCardById(6), player1));
        this.game.pickCard(game.getCardById(22), player1);
        assertEquals(1, player1.getAvailableActions().size());
        assertEquals(1, player1.getTribe().getDistinctInventorSymbols());
        assertEquals(1, player1.getTribe().getNumberByCharacterType(CharacterType.INVENTOR));
        assertEquals(2, player1.getFood());
    }

    /**
     * Test 4: Special Passive Effect (Bonus Action).
     * Verifies that the game logic can branch into special phases to handle passive building effects.
     * It tests:
     * - The state change redirecting the game flow into a DRAW_FROM_TOP_BONUS_ACTION phase upon activating a timed building.
     * - Maintenance of active player permissions and properties during this special execution.
     * - Clean resolution when the player concludes their turn, verifying that the phase goes back to
     *   the standard OFFER_TRACK_SELECTION phase.
     */
    @Test
    @Order(4)
    void bonusActionTest() {
        player1.getTribe().addCardToTribe(new TimedBuilding(1, 1, 1, 1, new TimedEffect.BonusPickCard()));
        assertThrows(IllegalMoveException.class, () -> this.game.pickCard(game.getCardById(15), player1));
        this.game.pickCard(game.getCardById(1), player1);
        assertEquals(GamePhase.DRAW_FROM_TOP_BONUS_ACTION, game.getPhase());
        assertEquals(1, player1.getTribe().getNumberByCharacterType(CharacterType.HUNTER));
        assertEquals(1, player1.getAvailableActions().size());
        assertEquals(2, player1.getFood());
        assertEquals(player1, game.getCurrPlayer());
        this.game.endCurrentTurn(player1);
        assertEquals(player2, game.getCurrPlayer());
        assertEquals(GamePhase.OFFER_TRACK_SELECTION, game.getPhase());
    }

    /**
     * Test 5: Disconnection and Reconnection State Lifecycle.
     * Verifies network resilience and user availability tracking inside the state machine.
     * It tests:
     * - Disconnection modeling by translating a player to an INACTIVE status and checking turn skip logic.
     * - Intermediate state where a newly reconnected player is put into a WAITING state.
     * - Turn skipping logic for non-ACTIVE players.
     * - Reactivation logic triggered upon complete action resolution.
     */
    @Test
    @Order(5)
    void disconnectionLifecycleTest() {
        this.game.moveToInactive(player2);
        assertEquals(player1, game.getCurrPlayer());
        assertEquals(PlayerStatus.INACTIVE, player2.getStatus());
        this.game.moveToWait(player2);
        assertEquals(player1, game.getCurrPlayer());
        assertEquals(PlayerStatus.WAITING, player2.getStatus());
        this.game.placeTotemOnTrack(player1, 1);
        assertEquals(player1, game.getCurrPlayer());
        assertEquals(PlayerStatus.WAITING, player2.getStatus());
        assertEquals(GamePhase.ACTION_RESOLUTION, this.game.getPhase());
        this.game.pickCard(game.getCardById(19), player1);
        assertEquals(GamePhase.DRAW_FROM_TOP_BONUS_ACTION, game.getPhase());
        assertEquals(player1, game.getCurrPlayer());
        assertEquals(PlayerStatus.ACTIVE, player2.getStatus());
        this.game.endCurrentTurn(player1);
    }

    /**
     * Test 6: End Turn Edge Condition.
     * Forces special edge case via reflection (i.e., no available character card to pick) and verifies optional end turn action availability.
     * It tests:
     * - Voluntary turn skip trigger with return to order queue.
     * - Offer track clearing after disconnection.
     */
    @Test
    @Order(6)
    void endCurrentTurnTest() throws NoSuchFieldException, IllegalAccessException {
        this.game.placeTotemOnTrack(player1, 2);
        this.game.placeTotemOnTrack(player2, 1);
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        Field rowField = boardField.get(game).getClass().getDeclaredField("topRow");
        rowField.setAccessible(true);
        ((Row) (rowField.get(boardField.get(game)))).removeCharacters();
        this.game.endCurrentTurn(player2);
        assertEquals(player1, game.getCurrPlayer());
        this.game.moveToInactive(player1);
        assertEquals(player2, game.getCurrPlayer());
    }

    /**
     * Test 7: Game Ending Resolution.
     * Forces early game ending via reflection by calling resolve phase on the FINAL_COUNT
     * and verifies correct score updates.
     * It tests:
     * - Unchanged state of inactive players.
     * - Score update with standard endgame bonus logic and passive building effects.
     */
    @Test
    @Order(7)
    void gameEndingTest() throws NoSuchFieldException, IllegalAccessException {
        int previousPoints = player2.getPrestigePoints();
        player2.getTribe().addCardToTribe(new FinalBuilding(1, 1, 1, 0, new FinalEffect.FinalBonusPrestigePoints()));
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        this.game.setPhase(GamePhase.FINAL_COUNT);
        this.game.getPhase().resolvePhase(this.game, (Board) (boardField.get(game)));
        assertEquals(1, player1.getPrestigePoints());
        assertEquals(previousPoints + 3 + 1 + 25, player2.getPrestigePoints());
    }
}