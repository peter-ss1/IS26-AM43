package it.polimi.ingsw.am43.main;

import it.polimi.ingsw.am43.client.view.gui.GUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class GuiApplication extends Application {
    @Override
    public void start(Stage stage) {
        GUI gui = new GUI();
        gui.initialize(stage);
    }
}
