package detteproject.controller;

import org.hibernate.sql.ordering.antlr.Factory;

import detteproject.App;
import detteproject.Repository.List.ClientRepository;
import detteproject.Repository.List.UserRepository;
import detteproject.Repository.jpa.RepositoryJpaClient;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Impl.FactoryRepo;
import detteproject.core.Factory.Impl.FactoryService;
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
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientTableController extends BoutiquierController {

    @FXML
    private TableView<Client> clientTableView;

    @FXML
    private TableColumn<Client, String> nomColumn;

    @FXML
    private TableColumn<Client, String> telephoneColumn;

    @FXML
    private TableColumn<Client, String> adresseColumn;

    private ObservableList<Client> clientList;
    private ClientService clientService;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    @FXML
    private void initialize() {
        // Initialize columns
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        // Initialize client service
        FactoryRepo<Client> clientFactoryRepo = new FactoryRepo<>(Client.class);
        RepositoryJpaClient clientRepository = (RepositoryJpaClient) clientFactoryRepo.createRepository();

        FactoryService<Client> factoryService = new FactoryService<>(Client.class, clientRepository);
        clientService = (ClientService) factoryService.createService();

        User user = UserConnected.getUserConnected();
        String nom = user.getLogin();
        String mail = user.getEmail();
        Labelnom.setText(nom);
        Labelmail.setText(mail);
        // Fetch and set client data
        loadClientData();
    }

    private void loadClientData() {
        // Fetch the list of clients and convert to ObservableList
        clientList = FXCollections.observableArrayList(clientService.show());
        clientTableView.setItems(clientList);
    }

    @FXML
    public void handleListClients() {
        System.out.println("Lister Clients");
        // Additional logic for listing clients if necessary
    }

    public void addArticle() {
        try {
            App.setRoot("createclient");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
