package detteproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PrimaryController {

    // Exemple de lien avec un élément FXML
    @FXML
    private Label myLabel;

    @FXML
    private Button myButton;

    // Méthode appelée lors du clic sur un bouton
    @FXML
    private void handleButtonAction() {
        myLabel.setText("Bouton cliquésssssssssssss !");
    }
}
