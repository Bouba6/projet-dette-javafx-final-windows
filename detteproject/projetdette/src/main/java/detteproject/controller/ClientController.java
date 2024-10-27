package detteproject.controller;

import java.io.IOException;

import detteproject.App;
import javafx.fxml.FXML;

public class ClientController {

    @FXML
    protected void handleListDebt() {
        try {
            App.setRoot("detteListClient");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    protected void Deconnexion() {
        try {
            App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleListDetteValide() {

        try {
            App.setRoot("clientValideDette");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
