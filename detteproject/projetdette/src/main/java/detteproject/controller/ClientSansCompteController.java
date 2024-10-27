package detteproject.controller;

import detteproject.State.Role;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Repo.FactoryClient;
import detteproject.core.Factory.Repo.FactoryUser;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;
import detteproject.services.ClientService;
import detteproject.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ClientSansCompteController extends AdminController {

    @FXML
    private TextField nameField, emailField, passwordField, telephoneField;
    @FXML
    private Button buttonCreate;
    private ClientService clientService;
    private UserService userService;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    @FXML
    private void initialize() {

        FactoryClient FactoryClient = new FactoryClient();
        clientService = FactoryClient.getClientService();
        FactoryUser factoryUser = new FactoryUser();
        userService = factoryUser.getUserService();
        User user = UserConnected.getUserConnected();
        String nom = user.getLogin();
        String mail = user.getEmail();
        Labelnom.setText(nom);
        Labelmail.setText(mail);
    }

    Client client;

    public void searchClient() {
        String tel = telephoneField.getText();
        client = clientService.find(tel);
        if (client == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Client non trouvé");
            alert.setContentText("Le client n'a pas été trouvé dans la base de données.");
            alert.showAndWait();
        } else if (client.getUser() != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Client déjà inscrit");
            alert.setContentText("Le client a déjà un compte.");
            alert.showAndWait();
        } else {
            nameField.setDisable(false);
            emailField.setDisable(false);
            passwordField.setDisable(false);
            buttonCreate.setDisable(false);
        }
    }

    public void handleCreateUser() {

        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setLogin(name);
        user.setRole(Role.Client);

        user.onPrePersist();
        user.setUserCreate(UserConnected.getUserConnected());
        userService.save(user);
        clientService.update(client);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Client inscrit");
        alert.setContentText("Le client a été inscrit dans la base de données.");
        alert.showAndWait();

        clearForm();
    }

    private void clearForm() {

        nameField.clear();
        emailField.clear();
        passwordField.clear();
        telephoneField.clear();
        nameField.setDisable(true);
        emailField.setDisable(true);
        passwordField.setDisable(true);
        buttonCreate.setDisable(true);
    }
}
