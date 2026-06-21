package it.polimi.ingsw.am43.client.view.gui;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Main JavaFX Application runner for the GUI client.
 * Manages the application lifecycle and background worker thread pool.
 */
public class GuiApplication extends Application {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /**
     * Initializes the GUI orchestrator and loads required custom asset fonts.
     */
    @Override
    public void start(Stage stage) {
        new GUI(this, stage);
        Font.loadFont(getClass().getResourceAsStream("/it/polimi/ingsw/am43/fonts/christmas-chalk.regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/it/polimi/ingsw/am43/fonts/FontsFree-Net-comic2.ttf"), 14);
    }

    /**
     * Shuts down the background executor service upon application termination.
     */
    @Override
    public void stop() {
        this.executor.shutdown();
    }

    /**
     * Submits a task to be executed asynchronously on the background thread pool.
     */
    public void runAsync(Runnable r) {
        executor.submit(r);
    }
}
