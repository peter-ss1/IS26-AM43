package it.polimi.ingsw.am43.main;

import it.polimi.ingsw.am43.client.view.gui.GuiApplication;
import it.polimi.ingsw.am43.client.view.tui.TUI;
import javafx.application.Application;

import java.util.Scanner;

/**
 * Main entry point for launching the game client application.
 */
public class ClientMain {
    /**
     * Prompts the user to select either the GUI or the TUI and launches the chosen interface.
     */
    static void main() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose UI: [1] GUI -- [2] TUI");
            System.out.print("> ");
            String uiChoice = scanner.nextLine().trim();
            if (uiChoice.equals("1")) {
                Application.launch(GuiApplication.class);
                break;
            } else if (uiChoice.equals("2")) {
                new Thread(new TUI(scanner)).start();
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }
}
