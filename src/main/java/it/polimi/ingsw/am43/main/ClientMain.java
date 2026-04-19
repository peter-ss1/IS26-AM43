package it.polimi.ingsw.am43.main;

import it.polimi.ingsw.am43.client.view.TUI;
import it.polimi.ingsw.am43.controller.ClientController;

import java.util.Scanner;

public class ClientMain {

    static void main() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose UI: [1] GUI -- [2] TUI");
        System.out.print("> ");

        String interfaceChoice = scanner.nextLine().trim();

        if (interfaceChoice.equals("1")) {
            //startGUI();
        } else if (interfaceChoice.equals("2")) {
            new TUI(new ClientController()).run(scanner);
        } else {
            System.out.println("Invalid choice. Exiting...");
        }
    }
}
