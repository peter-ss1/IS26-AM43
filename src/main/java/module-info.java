module it.polimi.ingsw.am43 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires java.logging;

    opens it.polimi.ingsw.am43 to javafx.fxml;
    exports it.polimi.ingsw.am43.model.player;
    exports it.polimi.ingsw.am43.model.cards;
    opens it.polimi.ingsw.am43.model.cards to javafx.fxml, com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.am43.model.board;
    opens it.polimi.ingsw.am43.model.board to javafx.fxml;

    opens it.polimi.ingsw.am43.model.utils.DTOs to com.fasterxml.jackson.databind;

    exports it.polimi.ingsw.am43.model.enums;

    opens it.polimi.ingsw.am43.model.player to org.junit.platform.commons;
}