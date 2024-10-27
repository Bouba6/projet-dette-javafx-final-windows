package detteproject.controller;

import java.io.IOException;

import detteproject.App;
import detteproject.State.Etat;
import detteproject.State.Role;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Repo.FactoryClient;
import detteproject.core.Factory.Repo.FactoryUser;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;
import detteproject.services.ClientService; // Assurez-vous d'importer votre service client
import detteproject.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class CreateClientController extends BoutiquierController {
    @FXML
    private TextField nameField, loginField, emailField, passwordField;
    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private Label Login, Email, password;

    @FXML
    private CheckBox addUserCheckBox;

    private ClientService clientService;
    private UserService userService;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    // Assurez-vous de créer et d'initialiser ce service

    @FXML
    public void initialize() {

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

    @FXML
    public void handleCreateClient() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String login = loginField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        Client client = new Client();
        client.setNom(name);
        client.setTelephone(phone);
        client.setAdresse(address);
        if (addUserCheckBox.isSelected()) {
            User user = new User();
            user.setEmail(email);
            user.setLogin(login);
            user.setPassword(password);
            client.setUser(user);
            user.setClient(client);
            user.setRole(Role.Client);
            user.setEtat(Etat.Activer);
            user.onPrePersist();
            user.setUserCreate(UserConnected.getUserConnected());
            userService.save(user);
            clientService.save(client);
        }

        // Enregistrer le client à l'aide du service
        clientService.save(client);
        if (clientService.save(client)) {
            System.out.println("Client créé avec succès : " + client.getNom());
            try {
                App.setRoot("clientTable");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } // Assurez-vous que cette méthode existe dans votre service

        // Afficher un message de succès ou faire autre chose après la création

        // Charger la vue précédente ou montrer un message de confirmation
    }

    @FXML
    private void toggleUserFields() {
        boolean isSelected = addUserCheckBox.isSelected();
        // Toggle visibility of user-related fields
        Login.setDisable(!isSelected);
        loginField.setDisable(!isSelected);
        Email.setDisable(!isSelected);
        emailField.setDisable(!isSelected);
        password.setDisable(!isSelected);
        passwordField.setDisable(!isSelected);
    }

    @FXML
    private void handleCancel() {
        try {
            App.setRoot("boutiquier");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
