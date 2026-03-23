module it.polimi.ingsw.am43 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens it.polimi.ingsw.am43 to javafx.fxml;
    exports it.polimi.ingsw.am43;
    exports it.polimi.ingsw.am43.model.cards;
    opens it.polimi.ingsw.am43.model.cards to javafx.fxml;
    exports it.polimi.ingsw.am43.model.board;
    opens it.polimi.ingsw.am43.model.board to javafx.fxml;
    exports it.polimi.ingsw.am43.model.player;

}