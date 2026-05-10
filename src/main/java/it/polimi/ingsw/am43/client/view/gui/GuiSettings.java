package it.polimi.ingsw.am43.client.view.gui;

import it.polimi.ingsw.am43.client.view.ViewState;

public final class GuiSettings {
    private GuiSettings() {}

    public static final String CONNECTION_PATH = "/it/polimi/ingsw/am43/fxml/connection-view.fxml";
    public static final String LOBBY_CHOICE_PATH = "/it/polimi/ingsw/am43/fxml/lobby-choice-view.fxml";
    public static final String IN_LOBBY_PATH = "/it/polimi/ingsw/am43/fxml/in-lobby-view.fxml";
    public static final String IN_GAME_PATH = "/it/polimi/ingsw/am43/fxml/in-game-view.fxml";

    public static String getPath(ViewState scene) {
        String path = "";
        switch (scene) {
            case CONNECTION -> path = CONNECTION_PATH;
            case IN_LOBBY, IN_LOBBY_CHOICE -> path = IN_LOBBY_PATH;
            case LOBBY_CHOICE -> path = LOBBY_CHOICE_PATH;
            case IN_GAME -> path = IN_GAME_PATH;
        }
        return path;
    }
}
