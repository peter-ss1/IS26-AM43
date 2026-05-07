package it.polimi.ingsw.am43.model.board;

import it.polimi.ingsw.am43.model.cards.Card;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.player.Player;
import it.polimi.ingsw.am43.model.utils.GameObserver;

import java.io.Serializable;
import java.util.List;

public interface ModelInterface extends Serializable {

    int getNumPlayers();

    void addPlayer(String nickname, Color color);

    void placeTotemOnTrack(Player player, int position);

    Player getPlayerByName(String name);

    Card getCardById(int id);

    void pickCard(Card card, Player player);

    void endCurrentTurn(Player player);

    List<Color> getAvailableColors();

    void setObserver(GameObserver observer);

    List<Player> getPlayers();

    void startGame();
}
