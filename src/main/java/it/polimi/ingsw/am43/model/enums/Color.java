package it.polimi.ingsw.am43.model.enums;
import static it.polimi.ingsw.am43.client.view.tui.TextFormatting.*;

/**
 * Colors that can be assigned to players.
 */
public enum Color {
    BLACK,
    WHITE,
    RED,
    YELLOW,
    CYAN;

    /**
     * @return the ANSI escape code used to render this color in the text UI
     */
    public String getAnsiCode() {
        return ERROR;
    }
}
