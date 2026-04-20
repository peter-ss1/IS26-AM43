package it.polimi.ingsw.am43.main;

import it.polimi.ingsw.am43.client.view.TUI;
import it.polimi.ingsw.am43.controller.ClientController;

import java.rmi.RemoteException;
import java.util.Scanner;

public class ClientMain {

    static void main() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose UI: [1] GUI -- [2] TUI");
        String uiChoice = "";
        while (!uiChoice.equalsIgnoreCase("1") || !uiChoice.equalsIgnoreCase("2")) {
            System.out.print("> ");
            uiChoice = scanner.nextLine().trim();
            if (uiChoice.equals("1")) {
                // startGUI();
                break;
            } else if (uiChoice.equals("2")) {
                try {
                    new TUI(scanner).run();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }
}
