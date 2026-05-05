package it.polimi.ingsw.am43.client.view.gui.scenes;

import it.polimi.ingsw.am43.client.view.gui.GUI;
import it.polimi.ingsw.am43.controller.ClientController;

public abstract class CustomScene {
    protected GUI gui;
    protected ClientController controller;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void setController(ClientController controller) {
        this.controller = controller;
    }

    public void refreshFromModel() {
    }

    public void showInfo(String s) {
    }

    public void showError(String s) {
    }
}
