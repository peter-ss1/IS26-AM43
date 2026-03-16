module it.polimi.ingsw.am43 {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.polimi.ingsw.am43 to javafx.fxml;
    exports it.polimi.ingsw.am43;
}