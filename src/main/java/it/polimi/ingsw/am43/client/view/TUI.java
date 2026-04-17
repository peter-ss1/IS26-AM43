package it.polimi.ingsw.am43.client.view;

import it.polimi.ingsw.am43.controller.ClientController;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class TUI extends UI {
    private final ClientController controller;

    public TUI(ClientController controller) {
        this.controller = controller;
        this.controller.setUi(this);
    }

    public void run(Scanner scanner) {
        System.out.println("Choose Connection Type:");
        System.out.println("1. RMI");
        System.out.println("2. Socket");
        System.out.print("> ");

        String connectionChoice = scanner.nextLine().trim();

        if (connectionChoice.equals("1")) {
            System.out.println("Starting TUI with RMI...");
            try {
                controller.chooseConnectionType(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (connectionChoice.equals("2")) {
            System.out.println("Starting TUI with Sockets...");
            try {
                controller.chooseConnectionType(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Invalid connection choice.");
        }

        while (true) {
            System.out.print("> ");

            String message = scanner.nextLine().trim();
            try {
                controller.sendString(message);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
