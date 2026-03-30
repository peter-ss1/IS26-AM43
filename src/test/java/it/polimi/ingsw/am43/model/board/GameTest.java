package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.TimedBuilding;
import it.polimi.ingsw.am43.model.cards.TimedEffect;
import it.polimi.ingsw.am43.model.enums.CharacterType;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.GamePhase;
import it.polimi.ingsw.am43.model.enums.OfferAction;
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
    private Game game;
    private Player player1;
    private Player player2;

    @BeforeAll
    void gameInitTest() {
        game = new Game(2, "pippo", Color.BLACK);
        assertEquals(GamePhase.PREPARATION, game.getPhase());
        assertThrows(IllegalPlayerInitializationException.class, () -> game.addPlayer("pippo", Color.WHITE));
        assertThrows(IllegalPlayerInitializationException.class, () -> game.addPlayer("jonny", Color.BLACK));
        game.addPlayer("jonny", Color.RED);
        assertFalse(game.getAvailableColors().contains(Color.RED) || game.getAvailableColors().contains(Color.BLACK));
        assert game.getPhase().equals(GamePhase.OFFER_TRACK_SELECTION);
        assertEquals(2, game.getPlayers().size());
        List<String> namePlayers = new ArrayList<>();
        namePlayers.add("pippo");
        namePlayers.add("jonny");
        for (Player player : game.getPlayers()) {
            assertTrue(namePlayers.contains(player.getNickname()));
        }
        assertNotNull(game.getCurrPlayer());
        assertTrue(namePlayers.contains(game.getCurrPlayer().getNickname()));
        for (int i = 1; i <= 117; i++) {//just to test, then adjust
            assertNotNull(game.getCardById(i));
            assertEquals(i, game.getCardById(i).getId());
        }
        assertEquals(2, game.getCurrPlayer().getFood());
        assertThrows(IllegalArgumentException.class, () -> game.getCardById(200));
    }

    @Test
    @Order(1)
    void offerSelectionTest() throws RuntimeException {
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

    @Test
    @Order(2)
    void actionResolutionTest() throws RuntimeException {
        //System.out.println(game.getVisibleIds());
        assertThrows(IllegalArgumentException.class, () -> game.pickCard(game.getCardById(6), new Player("error", Color.WHITE)));
        assertThrows(IllegalStateException.class, () -> game.addPlayer("pippo", Color.WHITE));
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

    @Test
    @Order(3)
    void bonusBuildingTest() {
        player1.getTribe().addCardToTribe(new TimedBuilding(1, 1, 1, 1, new TimedEffect.BonusPickCard()));
        assertThrows(IllegalMoveException.class, () -> this.game.pickCard(game.getCardById(15), player1));
        this.game.pickCard(game.getCardById(29), player1);
        assertEquals(GamePhase.DRAW_FROM_TOP_BONUS_ACTION, game.getPhase());
        assertEquals(1, player1.getTribe().getNumberByCharacterType(CharacterType.SHAMAN));
        assertEquals(1, player1.getShamanStars());
        assertEquals(1, player1.getAvailableActions().size());
        assertEquals(1, player1.getFood());
        assertEquals(player1, game.getCurrPlayer());
        this.game.endCurrentTurn(player1);
        assertEquals(player2, game.getCurrPlayer());
        assertEquals(GamePhase.OFFER_TRACK_SELECTION, game.getPhase());
    }

    @Test
    @Order(4)
    void endCurrentTurnTest() throws NoSuchFieldException, IllegalAccessException {
        this.game.placeTotemOnTrack(player2, 3);
        this.game.placeTotemOnTrack(player1, 1);
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        Field rowField = boardField.get(game).getClass().getDeclaredField("topRow");
        rowField.setAccessible(true);
        ((Row) (rowField.get(boardField.get(game)))).removeCharacters();
        this.game.endCurrentTurn(player1);
        assertEquals(player2, game.getCurrPlayer());
    }

}