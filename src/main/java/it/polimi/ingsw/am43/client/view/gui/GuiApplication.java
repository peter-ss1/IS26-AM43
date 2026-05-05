package it.polimi.ingsw.am43.client.view.gui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuiApplication extends Application {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Override
    public void start(Stage stage) {
        new GUI(this, stage);
    }

    @Override
    public void stop() {
        this.executor.shutdown();
    }

    public void runAsync(Runnable r) {
        executor.submit(r);
    }
}
