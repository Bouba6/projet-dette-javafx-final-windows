package detteproject.controller;

import java.io.IOException;

import detteproject.App;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Repo.FactoryUser;
import detteproject.data.entities.User;
import detteproject.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginController {
    private UserService userService;

    @FXML
    private void initialize() {
        FactoryUser factoryUser = new FactoryUser();
        userService = factoryUser.getUserService();
    }

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel; // Label pour afficher les erreurs
    @FXML
    private AnchorPane rootPane; // Pour ancrer la vue

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Désactiver le bouton de connexion pour éviter les clics multiples
        loginButton.setDisable(true);

        // Tenter de trouver l'utilisateur
        User user = userService.findByLogin(username, password);
        UserConnected.setUserConnected(user);
        if (user != null) {
            System.out.println("Login successful!");
            System.out.println("User role: " + user.getRole());
            switch (user.getRole()) {
                case Boutiquier:

                    try {
                        System.out.println("entered");
                        System.out.println("meewwwwwwwwwwwwwwwwwwww");
                        App.setRoot("DetteList");
                    } catch (IOException e) {
                        // Handle the exception, for example:
                        System.out.println("Error setting root: " + e.getMessage());
                        e.printStackTrace();
                    }

                    break;

                case Admin:
                    try {
                        System.out.println("entered");
                        App.setRoot("userTable");
                    } catch (IOException e) {
                        // Handle the exception, for example:
                        System.out.println("Error setting root: " + e.getMessage());
                    }

                    break;

                case Client:
                    try {
                        System.out.println("entered");
                        App.setRoot("detteListClient");
                    } catch (IOException e) {
                        // Handle the exception, for example:
                        System.out.println("Error setting root: " + e.getMessage());
                    }

                default:
                    break;
            }
            // Charger la prochaine vue
            // App.setRoot("NextView"); // Assurez-vous de décommenter et de mettre le bon
            // nom
        } else {
            errorLabel.setText("Invalid username or password!"); // Afficher l'erreur
            System.out.println("Invalid username or password!");
        }

        // Réactiver le bouton de connexion après la tentative
        loginButton.setDisable(false);
    }
}
