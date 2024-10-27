package detteproject.controller;

import detteproject.core.UserConnected;
import detteproject.core.Factory.Repo.FactoryClient;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;
import detteproject.services.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientInfoController extends BoutiquierController {

    @FXML
    private TextField clientNumberTextField;

    @FXML
    private TableView<Client> clientTableView;

    @FXML
    private TableColumn<Client, String> nomColumn;

    @FXML
    private TableColumn<Client, String> telephoneColumn;

    @FXML
    private TableColumn<Client, String> adresseColumn;

    private ClientService clientService;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    @FXML
    private void initialize() {

        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        // Initialize client service
        FactoryClient factoryClient = new FactoryClient();
        clientService = factoryClient.getClientService();

        User user = UserConnected.getUserConnected();
        String nom = user.getLogin();
        String mail = user.getEmail();
        Labelnom.setText(nom);
        Labelmail.setText(mail);
    }

    @FXML
    public void handleClientInfo() {
        String clientNumber = clientNumberTextField.getText();
        if (!clientNumber.isEmpty()) {
            // Logic to find client by number and update the TableView
            Client client = clientService.find(clientNumber); // Assuming this method exists
            if (client != null) {
                // Clear existing data and add the found client to the table
                ObservableList<Client> clientList = FXCollections.observableArrayList(client);
                clientTableView.setItems(clientList);
            } else {
                System.out.println("Client not found");
            }
        } else {
            System.out.println("Please enter a client number");
        }
    }
}
