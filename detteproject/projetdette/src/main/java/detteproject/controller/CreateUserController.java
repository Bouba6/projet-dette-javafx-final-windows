package detteproject.controller;

import detteproject.App;
import detteproject.State.Etat;
import detteproject.State.Role;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Repo.FactoryUser;
import detteproject.data.entities.User;
import detteproject.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateUserController extends AdminController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    private UserService userService;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    @FXML
    public void initialize() {
        // Initialize the role ComboBox with options
        roleComboBox.getItems().addAll("Boutiquier", "Admin", "Client");
        roleComboBox.getSelectionModel().selectFirst(); // Select the first role by default

        // Create the user service instance (assuming it is set up similarly to
        // ArticleService)
        FactoryUser factoryUser = new FactoryUser();
        userService = factoryUser.getUserService();

        User user = UserConnected.getUserConnected();
        String nom = user.getLogin();
        String mail = user.getEmail();
        Labelnom.setText(nom);
        Labelmail.setText(mail);
    }

    @FXML
    private void handleCreateUser(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();
        String status = "Activer"; // Default status is "Active"

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all the fields.");
            return;
        }

        // Create a new User object
        User newUser = new User();
        Role selectedRole = Role.valueOf(role);
        Etat statusEnum = Etat.valueOf(status);

        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(selectedRole);
        newUser.setEtat(statusEnum);

        // Save the new user
        userService.save(newUser);

        showAlert(Alert.AlertType.INFORMATION, "Success", "User created successfully!");

        // Clear the form fields after creating the user
        clearForm();
    }

    private void clearForm() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().selectFirst(); // Reset role selection
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            App.setRoot("userList"); // Navigate back to the user list view
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
